package com.ermr.eromoro.util;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import static java.lang.Math.*;
import java.math.BigDecimal;
import java.net.URI;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Open Topo Data 공개 API 클라이언트.
 *
 * - 엔드포인트: {baseUrl}/v1/{dataset}?locations=lat,lon|lat,lon
 * - 위치 형식: 반드시 "lat,lon" (OSRM은 "lon,lat" 이므로 혼동 주의)
 * - 공개 API 제한: 요청당 최대 100 locations, 초당 1회, 하루 1000회 (2025-10 기준)
 * - elevation 값이 null일 수 있음(데이터셋 범위 밖/NODATA). 호출자는 null 처리 필요.
 *
 * 예시 application.yml:
 * opentopo:
 *   base-url: https://api.opentopodata.org/v1
 *   dataset: aster30m    # 또는 srtm90m, etopo1, gebco2020 등
 *   connect-timeout-ms: 3000
 *   read-timeout-ms: 5000
 */
@Component
@RequiredArgsConstructor
public class ElevationClient {

    private final WebClient webClient = WebClient.builder().build();

    @Value("${opentopo.base-url:https://api.opentopodata.org/v1}")
    private String baseUrl;

    /** 사용하려는 데이터셋 이름 (예: aster30m, srtm90m, etopo1, gebco2020) */
    @Value("${opentopo.dataset:aster30m}")
    private String dataset;

    /** 연결/응답 타임아웃 (ms) */
    @Value("${opentopo.connect-timeout-ms:3000}")
    private int connectTimeoutMs;

    @Value("${opentopo.read-timeout-ms:5000}")
    private int readTimeoutMs;

    /** 공개 API 제약에 맞춘 배치 크기 */
    public static final int MAX_LOCATIONS_PER_REQUEST = 100;

    /**
     * 단일 지점 고도 조회
     */
    public ElevationPoint elevationAt(BigDecimal lat, BigDecimal lon) {
        if (lat == null || lon == null) {
            throw new IllegalArgumentException("lat/lon must not be null");
        }
        List<ElevationPoint> result = elevations(Collections.singletonList(new ElevationPoint(lat, lon, null)));
        return result.isEmpty() ? new ElevationPoint(lat, lon, null) : result.get(0);
    }

    /**
     * 다중 지점 고도 조회 (요청당 100개로 자동 분할)
     */
    public List<ElevationPoint> elevations(List<ElevationPoint> points) {
        if (points == null || points.isEmpty()) return Collections.emptyList();

        List<ElevationPoint> out = new ArrayList<>(points.size());
        int from = 0;
        while (from < points.size()) {
            int to = Math.min(from + MAX_LOCATIONS_PER_REQUEST, points.size());
            List<ElevationPoint> chunk = points.subList(from, to);
            out.addAll(fetchChunk(chunk));
            from = to;

            // 공개 API는 초당 1회 권장. 내부에서 짧게 sleep 하여 예의상 조절(필요시 비활성화 가능)
            try {
                Thread.sleep(1050L);
            } catch (InterruptedException ignored) {}
        }
        return out;
    }

    /**
     * 단일 요청(최대 100개)으로 OpenTopo API 호출
     */
    private List<ElevationPoint> fetchChunk(List<ElevationPoint> chunk) {
        String locationsParam = buildLocationsParam(chunk);

        URI uri = UriComponentsBuilder
                .fromHttpUrl(ensureNoTrailingSlash(baseUrl))
                .pathSegment("v1", dataset)
                .queryParam("locations", locationsParam)
                // 필요시 nodata_value, interpolation, units 등 파라미터 추가 가능
                .build(true)
                .toUri();

        JsonNode json = webClient.get()
                .uri(uri)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .timeout(Duration.ofMillis(Math.max(100, readTimeoutMs)))
                .block();

        String status = json != null && json.has("status") ? json.get("status").asText() : null;
        if (!"OK".equalsIgnoreCase(status)) {
            String message = json != null && json.has("error") ? json.get("error").asText() : "UNKNOWN_ERROR";
            throw new IllegalStateException("OpenTopo API error. status=" + status + ", error=" + message);
        }

        JsonNode results = json.path("results");
        List<ElevationPoint> out = new ArrayList<>(chunk.size());
        int i = 0;
        for (JsonNode r : results) {
            BigDecimal elevation = r.get("elevation").isNull() ? null : r.get("elevation").decimalValue();

            // 응답에는 location(lat,lng)도 오지만, 요청 순서 기준으로 매칭하는 것이 안전
            ElevationPoint req = chunk.get(i++);
            out.add(new ElevationPoint(req.getLat(), req.getLon(), elevation));
        }
        return out;
    }

    private String buildLocationsParam(List<ElevationPoint> points) {
        // "lat,lon|lat,lon|..." 형식 (주의: lat,lon 순서)
        StringBuilder sb = new StringBuilder();
        for (ElevationPoint p : points) {
            if (!StringUtils.hasLength(p.latStr()) || !StringUtils.hasLength(p.lonStr())) {
                throw new IllegalArgumentException("Invalid lat/lon in points");
            }
            if (sb.length() > 0) sb.append('|');
            sb.append(p.latStr()).append(',').append(p.lonStr());
        }
        return sb.toString();
    }

    private String ensureNoTrailingSlash(String url) {
        if (url == null) throw new IllegalArgumentException("baseUrl is null");
        return url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
    }

    /**
     * 고도 포인트 DTO
     * - elevation == null 일 수 있음 (데이터셋 범위 밖 또는 NODATA)
     */
    @Getter
    @ToString
    public static class ElevationPoint {
        private BigDecimal lat;
        private BigDecimal lon;
        private BigDecimal elevation; // meters (dataset/설정에 따라 달라질 수 있음)

        public ElevationPoint() {}

        public ElevationPoint(BigDecimal lat, BigDecimal lon, BigDecimal elevation) {
            this.lat = lat;
            this.lon = lon;
            this.elevation = elevation;
        }

        String latStr() { return lat == null ? null : lat.stripTrailingZeros().toPlainString(); }
        String lonStr() { return lon == null ? null : lon.stripTrailingZeros().toPlainString(); }
    }

    /**
     * 두 지점 사이의 경사도를 계산한다.
     * - rise = e2 - e1 (m)
     * - run  = 두 지점의 수평거리 (Haversine; m)
     * - slopePercent = (rise / run) * 100
     * - slopeDegrees = atan(rise / run) 를 도(degree)로 변환
     *
     * @return SlopeResult (두 지점 고도, 수평거리, 상승량, 경사도 %/° 포함)
     */
    public SlopeResult slopeBetween(BigDecimal lat1, BigDecimal lon1,
                                    BigDecimal lat2, BigDecimal lon2) {

        // 1) 두 지점 고도 조회
        List<ElevationPoint> points = elevations(List.of(
                new ElevationPoint(lat1, lon1, null),
                new ElevationPoint(lat2, lon2, null)
        ));

        if (points.size() != 2) {
            throw new IllegalStateException("Elevation API returned unexpected result size: " + points.size());
        }

        ElevationPoint p1 = points.get(0);
        ElevationPoint p2 = points.get(1);

        BigDecimal e1 = p1.getElevation(); // meter (nullable)
        BigDecimal e2 = p2.getElevation(); // meter (nullable)
        if (e1 == null || e2 == null) {
            // 데이터셋 범위 밖이거나 NODATA일 수 있음
            throw new IllegalStateException("Elevation not available for one or both points.");
        }

        // 2) 수평거리 계산 (Haversine, WGS84 평균 반경)
        double horiz = haversineMeters(
                p1.getLat().doubleValue(), p1.getLon().doubleValue(),
                p2.getLat().doubleValue(), p2.getLon().doubleValue()
        );

        // 3) 경사 계산
        double rise = e2.doubleValue() - e1.doubleValue();   // m
        if (horiz <= 0.0) {
            // 동일 지점 또는 수평거리 0일 때: 수학적으로 무의미
            return new SlopeResult(e1, e2, 0.0, rise, null, null, Direction.FLAT);
        }

        double slope = rise / horiz;                         // ratio
        double slopePercent = slope * 100.0;
        double slopeDegrees = toDegrees(atan(slope));

        Direction dir = rise > 0 ? Direction.UPHILL : (rise < 0 ? Direction.DOWNHILL : Direction.FLAT);

        return new SlopeResult(
                e1, e2, horiz, rise,
                // 결과는 BigDecimal로도 쓰기 편하게 소수 6자리까지 반올림
                roundScale(slopePercent, 6),
                roundScale(slopeDegrees, 6),
                dir
        );
    }

    /**
     * Haversine 공식을 사용하여 두 위경도 지점 간의 수평거리(미터)를 계산.
     * 위도/경도는 degree 단위를 입력.
     */
    private double haversineMeters(double lat1, double lon1, double lat2, double lon2) {
        // WGS84 평균 지구 반경 (meter)
        final double R = 6371008.8;
        double dLat = toRadians(lat2 - lat1);
        double dLon = toRadians(lon2 - lon1);
        double a = sin(dLat / 2) * sin(dLat / 2)
                + cos(toRadians(lat1)) * cos(toRadians(lat2))
                * sin(dLon / 2) * sin(dLon / 2);
        double c = 2 * atan2(sqrt(a), sqrt(1 - a));
        return R * c;
    }

    private BigDecimal roundScale(double v, int scale) {
        return new BigDecimal(Double.toString(v)).setScale(scale, java.math.RoundingMode.HALF_UP);
    }

    /**
     * 경사 결과 DTO
     */
    @Getter
    @ToString
    public static class SlopeResult {
        /** 시작/끝 지점의 고도 (m) */
        private final BigDecimal elevationStart;
        private final BigDecimal elevationEnd;

        /** 두 지점 간 수평거리 (m) */
        private final double horizontalMeters;

        /** 상승량: e2 - e1 (m) */
        private final double riseMeters;

        /** 경사도(%) = (rise/run)*100. 수평거리 0이면 null */
        private final BigDecimal slopePercent;

        /** 경사각(°) = atan(rise/run)·(180/π). 수평거리 0이면 null */
        private final BigDecimal slopeDegrees;

        /** 진행 방향: 오르막/내리막/평지 */
        private final Direction direction;

        public SlopeResult(BigDecimal elevationStart, BigDecimal elevationEnd,
                           double horizontalMeters, double riseMeters,
                           BigDecimal slopePercent, BigDecimal slopeDegrees,
                           Direction direction) {
            this.elevationStart = elevationStart;
            this.elevationEnd = elevationEnd;
            this.horizontalMeters = horizontalMeters;
            this.riseMeters = riseMeters;
            this.slopePercent = slopePercent;
            this.slopeDegrees = slopeDegrees;
            this.direction = direction;
        }
    }

    public enum Direction {
        UPHILL, DOWNHILL, FLAT
    }
}
