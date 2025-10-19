package com.ermr.eromoro.util;

import com.ermr.eromoro.dto.CourseRequestDto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class OsrmClient {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final WebClient webClient = WebClient.builder().build();

    @Value("${osrm.base-url}")
    private String baseUrl;

    @Value("${osrm.profile}")
    private String profile;

    /**
     * 원시 좌표 값을 OSRM 도로망 내 가장 가까운 점으로 snap.
     *
     * @param raw 원시 좌표
     * @return Snapped
     */
    public CourseRequestDto.LatLon nearest(CourseRequestDto.LatLon raw) {
        String coord = raw.getLon().toPlainString() + "," + raw.getLat().toPlainString();
        String url = String.format("%s/nearest/v1/%s/%s", baseUrl, profile, encode(coord));

        JsonNode json = webClient.get()
                .uri(url)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();

        JsonNode wp = json.path("waypoints").get(0);
        JsonNode loc = wp.path("location"); // [lon, lat]
        BigDecimal lon = loc.get(0).decimalValue();
        BigDecimal lat = loc.get(1).decimalValue();

        CourseRequestDto.LatLon snapped = new CourseRequestDto.LatLon();
        snapped.setLat(lat);
        snapped.setLon(lon);
        return snapped;
    }

    public RouteResult route(CourseRequestDto.LatLon start, CourseRequestDto.LatLon end) {
        String coords = start.getLon().toPlainString() + "," + start.getLat().toPlainString()
                + ";" + end.getLon().toPlainString() + "," + end.getLat().toPlainString();

        String url = String.format(
                "%s/route/v1/%s/%s?overview=full&geometries=geojson&steps=false&annotations=distance",
                baseUrl, profile, encode(coords)
        );

        JsonNode json = webClient.get()
                .uri(url)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();

        JsonNode route = json.path("routes").get(0);
        double distance = route.path("distance").asDouble(); // meters
        double duration = route.path("duration").asDouble(); // seconds

        // geometry.coordinates: [ [lon,lat], [lon,lat], ... ]
        List<CourseRequestDto.LatLon> points = new ArrayList<>();
        JsonNode coordsNode = route.path("geometry").path("coordinates");
        for (JsonNode c : coordsNode) {
            BigDecimal lon = c.get(0).decimalValue();
            BigDecimal lat = c.get(1).decimalValue();
            CourseRequestDto.LatLon p = new CourseRequestDto.LatLon();
            p.setLat(lat);
            p.setLon(lon);
            points.add(p);
        }

        return new RouteResult(distance, duration, points);
    }

    private String encode(String s) {
        return URLEncoder.encode(s, StandardCharsets.UTF_8);
    }

    @lombok.Value
    public static class RouteResult {
        double distanceMeters;
        double durationSeconds;
        List<CourseRequestDto.LatLon> geometry;
    }
}