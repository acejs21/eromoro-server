package com.ermr.eromoro.service;

import com.ermr.eromoro.apiResponse.exception.handler.CourseHandler;
import com.ermr.eromoro.apiResponse.exception.handler.UserHandler;
import com.ermr.eromoro.apiResponse.status.ErrorStatus;
import com.ermr.eromoro.domain.Course;
import com.ermr.eromoro.domain.CoursePoint;
import com.ermr.eromoro.domain.User;
import com.ermr.eromoro.dto.CourseRequestDto;
import com.ermr.eromoro.repository.CoursePointRepository;
import com.ermr.eromoro.repository.CourseRepository;
import com.ermr.eromoro.repository.UserRepository;
import com.ermr.eromoro.util.OsrmClient;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    private final CoursePointRepository coursePointRepository;
    private final OsrmClient osrmClient;
    private final UserRepository userRepository;

    @Transactional
    public List<Course> generateCourse(CourseRequestDto.GenerateDto request, Long userId) {
        final int TARGET_COUNT = 3;
        final int TOL_M = 80; // 종단 검증 허용 오차(미터)

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserHandler(ErrorStatus.USER_NOT_FOUND));

        // 1) 요청 좌표(불변)
        CourseRequestDto.LatLon startOrig = request.getStart();
        CourseRequestDto.LatLon endOrig   = request.getEnd();

        // 2) 라우팅 전용 스냅 좌표
        CourseRequestDto.LatLon routingStart = osrmClient.nearest(startOrig);
        CourseRequestDto.LatLon routingEnd   = osrmClient.nearest(endOrig);

        int targetMin = request.getTargetDurationMin() != null ? request.getTargetDurationMin() : 0;
        int baseOffsetM = Math.min(600, Math.max(60, targetMin * 20));

        CourseRequestDto.LatLon mid  = midpoint(routingStart, routingEnd);
        CourseRequestDto.LatLon midL = offsetNormal(mid, routingStart, routingEnd, +baseOffsetM);
        CourseRequestDto.LatLon midR = offsetNormal(mid, routingStart, routingEnd, -baseOffsetM);
        CourseRequestDto.LatLon sL   = offsetNormal(routingStart, routingStart, routingEnd, +baseOffsetM / 2);
        CourseRequestDto.LatLon sR   = offsetNormal(routingStart, routingStart, routingEnd, -baseOffsetM / 2);

        List<Course> out = new ArrayList<>(TARGET_COUNT);

        // 직선
        tryRouteValidateNormalizeAndSave(out, user, startOrig, endOrig, routingStart, routingEnd, List.of(routingStart, routingEnd), TOL_M);

        // 좌/우
        if (out.size() < TARGET_COUNT)
            tryRouteValidateNormalizeAndSave(out, user, startOrig, endOrig, routingStart, routingEnd, List.of(routingStart, midL, routingEnd), TOL_M);
        if (out.size() < TARGET_COUNT)
            tryRouteValidateNormalizeAndSave(out, user, startOrig, endOrig, routingStart, routingEnd, List.of(routingStart, midR, routingEnd), TOL_M);

        // 예비
        if (out.size() < TARGET_COUNT)
            tryRouteValidateNormalizeAndSave(out, user, startOrig, endOrig, routingStart, routingEnd, List.of(routingStart, sL, routingEnd), TOL_M);
        if (out.size() < TARGET_COUNT)
            tryRouteValidateNormalizeAndSave(out, user, startOrig, endOrig, routingStart, routingEnd, List.of(routingStart, sR, routingEnd), TOL_M);

        // 부족하면 직선 재사용(정규화 적용되므로 시작/끝은 항상 원본)
        while (out.size() < TARGET_COUNT) {
            OsrmClient.RouteResult r = osrmClient.route(List.of(routingStart, routingEnd));
            if (r == null) break;
            if (!endsNear(r, routingEnd, TOL_M)) break;
            List<CourseRequestDto.LatLon> norm = normalizeEndpoints(r.getGeometry(), startOrig, endOrig);
            out.add(buildAndSaveCourse(user, startOrig, endOrig, r, norm));
        }

        if (out.size() > TARGET_COUNT) out = out.subList(0, TARGET_COUNT);
        return out;
    }

    // 라우팅 → 검증 → 정규화 → 저장
    private void tryRouteValidateNormalizeAndSave(List<Course> bucket,
                                                  User user,
                                                  CourseRequestDto.LatLon startOrig,
                                                  CourseRequestDto.LatLon endOrig,
                                                  CourseRequestDto.LatLon routingStart,
                                                  CourseRequestDto.LatLon routingEnd,
                                                  List<CourseRequestDto.LatLon> wps,
                                                  int tolM) {
        if (bucket.size() >= 3) return;
        OsrmClient.RouteResult r = osrmClient.route(wps);
        if (r == null || r.getGeometry() == null || r.getGeometry().size() < 2) return;
        if (!endsNear(r, routingEnd, tolM)) return; // 종착 검증 실패 시 폐기
        List<CourseRequestDto.LatLon> normalized = normalizeEndpoints(r.getGeometry(), startOrig, endOrig);
        bucket.add(buildAndSaveCourse(user, startOrig, endOrig, r, normalized));
    }

    // 종착 검증: 라우팅 결과 마지막 점이 스냅 종착점과 tolM 이내?
    private boolean endsNear(OsrmClient.RouteResult r, CourseRequestDto.LatLon routingEnd, int tolM) {
        List<CourseRequestDto.LatLon> g = r.getGeometry();
        CourseRequestDto.LatLon last = g.get(g.size() - 1);
        return haversineMeters(last, routingEnd) <= tolM;
    }

    // 정규화: 폴리라인의 첫/마지막 점을 요청 좌표로 강제
    private List<CourseRequestDto.LatLon> normalizeEndpoints(List<CourseRequestDto.LatLon> geom,
                                                             CourseRequestDto.LatLon startOrig,
                                                             CourseRequestDto.LatLon endOrig) {
        List<CourseRequestDto.LatLon> out = new ArrayList<>(geom.size() + 2);
        // 시작점이 충분히 가깝지 않다면 맨 앞에 startOrig 삽입, 그 외엔 교체
        if (haversineMeters(geom.get(0), startOrig) > 5) out.add(startOrig);
        else out.add(startOrig); // 교체
        // 본문
        if (geom.size() > 2) out.addAll(geom.subList(1, geom.size() - 1));
        // 마지막도 동일 처리
        if (!out.isEmpty() && haversineMeters(geom.get(geom.size() - 1), endOrig) <= 5) {
            out.add(endOrig); // 교체
        } else {
            out.add(endOrig); // 삽입/교체 동일 효과
        }
        return out;
    }

    // 해버사인 거리(m)
    private double haversineMeters(CourseRequestDto.LatLon a, CourseRequestDto.LatLon b) {
        double lat1 = Math.toRadians(a.getLat().doubleValue());
        double lon1 = Math.toRadians(a.getLon().doubleValue());
        double lat2 = Math.toRadians(b.getLat().doubleValue());
        double lon2 = Math.toRadians(b.getLon().doubleValue());
        double dlat = lat2 - lat1, dlon = lon2 - lon1;
        double h = Math.sin(dlat/2)*Math.sin(dlat/2) + Math.cos(lat1)*Math.cos(lat2)*Math.sin(dlon/2)*Math.sin(dlon/2);
        return 2 * 6371000 * Math.asin(Math.sqrt(h));
    }


    // 위경도 중간점
    private CourseRequestDto.LatLon midpoint(CourseRequestDto.LatLon a, CourseRequestDto.LatLon b) {
        BigDecimal lat = a.getLat().add(b.getLat()).divide(BigDecimal.valueOf(2), 7, RoundingMode.HALF_UP);
        BigDecimal lon = a.getLon().add(b.getLon()).divide(BigDecimal.valueOf(2), 7, RoundingMode.HALF_UP);
        return new CourseRequestDto.LatLon(lat, lon);
    }

    // a->b 방향의 수직(법선)으로 distanceM 만큼 pivot을 오프셋
    private CourseRequestDto.LatLon offsetNormal(CourseRequestDto.LatLon pivot,
                                                 CourseRequestDto.LatLon a,
                                                 CourseRequestDto.LatLon b,
                                                 int distanceM) {
        double latA = a.getLat().doubleValue(), lonA = a.getLon().doubleValue();
        double latB = b.getLat().doubleValue(), lonB = b.getLon().doubleValue();

        // 위경도 차이를 meter 벡터로 환산
        double avgLat = (latA + latB) / 2.0;
        double dy = (latB - latA) * 111_320.0;
        double dx = (lonB - lonA) * 111_320.0 * Math.cos(Math.toRadians(avgLat));

        // 수직(normal) 단위벡터
        double nx = -dy, ny = dx;
        double norm = Math.hypot(nx, ny);
        if (norm < 1e-6) norm = 1.0;
        nx /= norm; ny /= norm;

        // meter → 위경도 델타
        double dLat = (ny * distanceM) / 111_320.0;
        double dLon = (nx * distanceM) / (111_320.0 * Math.cos(Math.toRadians(pivot.getLat().doubleValue())));

        BigDecimal lat = pivot.getLat().add(BigDecimal.valueOf(dLat)).setScale(6, RoundingMode.HALF_UP);
        BigDecimal lon = pivot.getLon().add(BigDecimal.valueOf(dLon)).setScale(6, RoundingMode.HALF_UP);
        return new CourseRequestDto.LatLon(lat, lon);
    }

    // 저장 시, 라우팅의 거리/시간은 그대로 쓰되, geometry는 정규화된 것 사용
    private Course buildAndSaveCourse(User user,
                                      CourseRequestDto.LatLon startOrig,
                                      CourseRequestDto.LatLon endOrig,
                                      OsrmClient.RouteResult route,
                                      List<CourseRequestDto.LatLon> normalizedGeom) {

        float distanceKm = BigDecimal.valueOf(route.getDistanceMeters())
                .divide(BigDecimal.valueOf(1000), 1, RoundingMode.HALF_UP)
                .floatValue();
        int durationMin = (int) Math.round(route.getDurationSeconds() / 60.0);

        Course course = Course.builder()
                .user(user)
                .title(buildCourseTitle(startOrig, endOrig)) // 타이틀도 요청 좌표 기준
                .likeCount(0)
                .isPublic(false)
                .duration(durationMin)
                .distance(distanceKm)
                .build();

        int seq = 0;
        for (CourseRequestDto.LatLon p : normalizedGeom) {
            CoursePoint cp = CoursePoint.builder()
                    .seq(seq++)
                    .latitude(p.getLat())
                    .longitude(p.getLon())
                    .build();
            cp.setCourse(course);
        }
        return courseRepository.save(course);
    }

    private String buildCourseTitle(CourseRequestDto.LatLon start, CourseRequestDto.LatLon end) {
        return String.format("Course (%.5f, %.5f) → (%.5f, %.5f)",
                start.getLat().doubleValue(),
                start.getLon().doubleValue(),
                end.getLat().doubleValue(),
                end.getLon().doubleValue());
    }

    @Transactional(readOnly = true)
    public Course getCourse(Long courseId) {
        return courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseHandler(ErrorStatus.COURSE_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public List<CoursePoint> getCoursePointsOrdered(Long courseId) {
        return coursePointRepository.findByCourseIdOrderBySeqAsc(courseId);
    }
}
