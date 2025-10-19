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

}
