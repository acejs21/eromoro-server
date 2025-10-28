package com.ermr.eromoro.service;

import com.ermr.eromoro.apiResponse.exception.handler.CourseHandler;
import com.ermr.eromoro.apiResponse.exception.handler.UserHandler;
import com.ermr.eromoro.apiResponse.status.ErrorStatus;
import com.ermr.eromoro.domain.*;
import com.ermr.eromoro.dto.CourseRequestDto;
import com.ermr.eromoro.repository.*;
import com.ermr.eromoro.util.OsrmClient;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Polygon;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    private final CoursePointRepository coursePointRepository;
    private final OsrmClient osrmClient;
    private final UserRepository userRepository;
    private final FeedbackRepository feedbackRepository;
    private final GeometryFactory geometryFactory = new GeometryFactory();
    private final BarrierRepository barrierRepository;


    @Transactional
    public List<Course> generateCourse(CourseRequestDto.GenerateDto request, Long userId) {
        User getUser = userRepository.findById(userId)
                .orElseThrow(() -> new UserHandler(ErrorStatus.USER_NOT_FOUND));

        // OSRM 클라이언트 사용 코스 생성
        CourseRequestDto.LatLon snappedStart = osrmClient.nearest(request.getStart());
        CourseRequestDto.LatLon snappedEnd   = osrmClient.nearest(request.getEnd());

        OsrmClient.RouteResult route = osrmClient.route(snappedStart, snappedEnd);
        
        // distance KM 변환
        float distanceKm = BigDecimal.valueOf(route.getDistanceMeters())
                .divide(BigDecimal.valueOf(1000), 1, RoundingMode.HALF_UP)
                .floatValue();

        // duration Min 변환
        int durationMin = (int) (route.getDurationSeconds() / 60.0);

        // 코스 저장
        Course course = Course.builder()
                .user(getUser)
                .title(buildCourseTitle(snappedStart, snappedEnd))
                .likeCount(0)
                .isPublic(false)
                .duration(durationMin)
                .distance(distanceKm)
                .build();

        int seq = 0;
        for (CourseRequestDto.LatLon p : route.getGeometry()) {
            CoursePoint cp = CoursePoint.builder()
                    .seq(seq++)
                    .latitude(p.getLat())
                    .longitude(p.getLon())
                    .build();
            cp.setCourse(course); // 양방향 편의 메소드
//            course.addPoint(cp); // 양방향 편의 메서드
        }

        // 부모만 저장하면 자식이 Cascade로 함께 INSERT
        courseRepository.save(course);

        return List.of(course);
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

    /**
     * CoursePoint 리스트의 경계보다 넓은 범위 내의 Feedback 리스트를 조회합니다.
     * @param points 위도, 경도 좌표를 담고 있는 CoursePoint 객체 리스트
     * @return 확장된 경계 내에 있는 Feedback 리스트
     */
    public List<Feedback> getCourseFeedbacks(List<CoursePoint> points) {
        if (points == null || points.isEmpty()) {
            return Collections.emptyList();
        }
        // 확장된 좌표를 이용해 사각형(Polygon)을 생성합니다.
        Coordinate[] coordinates = getCoordinatesFromPoints(points);
        Polygon expandedBoundingBox = geometryFactory.createPolygon(coordinates);

        // Repository 메서드를 호출하여 확장된 영역 내의 Feedback을 조회합니다.
        return feedbackRepository.findByLocationWithin(expandedBoundingBox);
    }

    public List<Barrier> getCourseBarriers(List<CoursePoint> points) {
        if (points == null || points.isEmpty()) {
            return Collections.emptyList();
        }
        // 확장된 좌표를 이용해 사각형(Polygon)을 생성합니다.
        Coordinate[] coordinates = getCoordinatesFromPoints(points);
        Polygon expandedBoundingBox = geometryFactory.createPolygon(coordinates);

        return barrierRepository.findByLocationWithin(expandedBoundingBox);
    }

    private Coordinate[] getCoordinatesFromPoints(List<CoursePoint> points) {
        // 1. 위도와 경도의 최소/최대 값을 찾습니다.
        BigDecimal minLat = points.stream().map(CoursePoint::getLatitude).min(Comparator.naturalOrder()).orElseThrow();
        BigDecimal maxLat = points.stream().map(CoursePoint::getLatitude).max(Comparator.naturalOrder()).orElseThrow();
        BigDecimal minLon = points.stream().map(CoursePoint::getLongitude).min(Comparator.naturalOrder()).orElseThrow();
        BigDecimal maxLon = points.stream().map(CoursePoint::getLongitude).max(Comparator.naturalOrder()).orElseThrow();

        // 2. 검색 범위를 확장하기 위한 여유(margin) 값을 설정합니다.
        BigDecimal margin = new BigDecimal("0.01");

        // 3. 최소/최대 값에 margin을 적용하여 검색 범위를 확장합니다.
        BigDecimal expandedMinLat = minLat.subtract(margin);
        BigDecimal expandedMaxLat = maxLat.add(margin);
        BigDecimal expandedMinLon = minLon.subtract(margin);
        BigDecimal expandedMaxLon = maxLon.add(margin);

        // 4. 확장된 좌표를 이용해 사각형(Polygon)을 생성합니다.
        Coordinate[] coordinates = new Coordinate[]{
                new Coordinate(expandedMinLon.doubleValue(), expandedMinLat.doubleValue()), // 좌측 하단
                new Coordinate(expandedMaxLon.doubleValue(), expandedMinLat.doubleValue()), // 우측 하단
                new Coordinate(expandedMaxLon.doubleValue(), expandedMaxLat.doubleValue()), // 우측 상단
                new Coordinate(expandedMinLon.doubleValue(), expandedMaxLat.doubleValue()), // 좌측 상단
                new Coordinate(expandedMinLon.doubleValue(), expandedMinLat.doubleValue())  // 시작점으로 돌아와 닫기
        };

        return coordinates;
    }
}
