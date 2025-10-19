package com.ermr.eromoro.service;

import com.ermr.eromoro.apiResponse.exception.handler.UserHandler;
import com.ermr.eromoro.apiResponse.status.ErrorStatus;
import com.ermr.eromoro.domain.Course;
import com.ermr.eromoro.domain.LikeCourse;
import com.ermr.eromoro.domain.User;
import com.ermr.eromoro.domain.enums.CourseType;
import com.ermr.eromoro.repository.CourseRepository;
import com.ermr.eromoro.repository.LikeCourseRepository;
import com.ermr.eromoro.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

import static com.ermr.eromoro.domain.enums.CourseType.SPOT;

@Service
@RequiredArgsConstructor
public class CourseQueryServiceImpl implements CourseQueryService {

    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final LikeCourseRepository likeCourseRepository;

    @Override
    public List<Course> getCourseList(Long userId, CourseType courseType, String keyword, Long spotId) {
        User getUser = userRepository.findById(userId)
                .orElseThrow(() -> new UserHandler(ErrorStatus.USER_NOT_FOUND));
        switch (courseType) {
            case ALL:
                if (keyword == null || keyword.isBlank()) {
                    return courseRepository.findByUserOrderByCreatedAtDesc(getUser);
                }
                return courseRepository.findByUserAndTitleContainingIgnoreCaseOrderByCreatedAtDesc(getUser, keyword);
            case LIKE:
                if (keyword == null || keyword.isBlank()) {
                    return likeCourseRepository.findByUser(getUser).stream()
                            .map(LikeCourse::getCourse)
                            .sorted(Comparator.comparing(Course::getCreatedAt).reversed())
                            .toList();
                }
                return null;
//                return likeCourseRepository.findByUser(getUser).stream()
//                    .map(LikeCourse::getCourse)
//                    .sorted(Comparator.comparing(Course::getCreatedAt).reversed())
//                    .toList();
            case SPOT:
                return null;
            default:
                throw new UserHandler(ErrorStatus.INVALID_COURSE_TYPE);
        }
    }
}
