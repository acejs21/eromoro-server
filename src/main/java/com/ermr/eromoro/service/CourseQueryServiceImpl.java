package com.ermr.eromoro.service;

import com.ermr.eromoro.apiResponse.exception.handler.UserHandler;
import com.ermr.eromoro.apiResponse.status.ErrorStatus;
import com.ermr.eromoro.converter.CourseConverter;
import com.ermr.eromoro.domain.Course;
import com.ermr.eromoro.domain.LikeCourse;
import com.ermr.eromoro.domain.Spot;
import com.ermr.eromoro.domain.User;
import com.ermr.eromoro.domain.enums.CourseType;
import com.ermr.eromoro.dto.CourseResponseDto;
import com.ermr.eromoro.repository.CourseRepository;
import com.ermr.eromoro.repository.LikeCourseRepository;
import com.ermr.eromoro.repository.SpotRepository;
import com.ermr.eromoro.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

import static com.ermr.eromoro.domain.enums.CourseType.LIKE;
import static com.ermr.eromoro.domain.enums.CourseType.SPOT;

@Service
@RequiredArgsConstructor
public class CourseQueryServiceImpl implements CourseQueryService {

    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final LikeCourseRepository likeCourseRepository;
    private final SpotRepository spotRepository;

    @Override
    public CourseResponseDto.getCourseListResultDto getCourseList(Long userId, CourseType courseType, String keyword, Long spotId) {
        User getUser = userRepository.findById(userId)
                .orElseThrow(() -> new UserHandler(ErrorStatus.USER_NOT_FOUND));
        switch (courseType) {
            case ALL:
                if (keyword == null || keyword.isBlank()) {
                    return CourseConverter.getCourseResultDtoToGetCourseListResultDto(
                            courseRepository.findByUserWithLikeCourseOrderByCreatedAtDesc(getUser.getId())
                    );
                }
                return CourseConverter.getCourseResultDtoToGetCourseListResultDto(
                        courseRepository.findByUserWithLikeCourseAndTitleContainingOrderByCreatedAtDesc(getUser.getId(), keyword)
                );
            case LIKE:
                if (keyword == null || keyword.isBlank()) {
                    List<Course> courseList = likeCourseRepository.findByUser(getUser).stream()
                            .map(LikeCourse::getCourse)
                            .sorted(Comparator.comparing(Course::getCreatedAt).reversed())
                            .toList();
                    return CourseConverter.courseListToGetCourseListResultDto(courseList);
                }
                List<Course> courseList = likeCourseRepository.findByUser(getUser).stream()
                        .map(LikeCourse::getCourse)
                        .filter(c -> {
                            String t = c.getTitle();
                            return t != null && t.toLowerCase().contains(keyword.toLowerCase());
                        })
                        .sorted(Comparator.comparing(Course::getCreatedAt).reversed())
                        .toList();

                return CourseConverter.courseListToGetCourseListResultDto(courseList);
            case SPOT:
                if (spotId == null) {
                    throw new UserHandler(ErrorStatus.INVALID_COURSE_SPOT_ID);
                }
                Spot getSpot = spotRepository.findById(spotId)
                        .orElseThrow(() -> new UserHandler(ErrorStatus.SPOT_NOT_FOUND));
                if (keyword == null || keyword.isBlank()) {
                    return CourseConverter.getCourseResultDtoToGetCourseListResultDto(
                            courseRepository.findBySpotWithLikeCourseOrderByCreatedAtDesc(userId, getSpot.getId())
                    );
                }
                return CourseConverter.getCourseResultDtoToGetCourseListResultDto(
                        courseRepository.findBySpotWithLikeCourseAndTitleContainingOrderByCreatedAtDesc(userId, getSpot.getId(), keyword)
                );
            default:
                throw new UserHandler(ErrorStatus.INVALID_COURSE_TYPE);
        }
    }
}
