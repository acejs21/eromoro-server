package com.ermr.eromoro.converter;

import com.ermr.eromoro.domain.Course;
import com.ermr.eromoro.domain.CoursePoint;
import com.ermr.eromoro.domain.User;
import com.ermr.eromoro.domain.enums.CourseType;
import com.ermr.eromoro.domain.enums.UserType;
import com.ermr.eromoro.dto.CourseRequestDto;
import com.ermr.eromoro.dto.CourseResponseDto;

import java.util.List;
import java.util.stream.Collectors;

public class CourseConverter {

    public static CourseResponseDto.isCourseResultDto toIsCourseResultDto(User user) {
        return CourseResponseDto.isCourseResultDto.builder()
                .isCourse(user.isCourse())
                .build();
    }

    public static CourseResponseDto.saveCoursePointResultDto toSaveCoursePointResultDto (Course course) {
        return CourseResponseDto.saveCoursePointResultDto.builder()
                .courseId(course.getId())
                .build();
    }

    public static CourseResponseDto.getCourseListResultDto courseListToGetCourseListResultDto(
            List<Course> courseList
    ) {
        List<CourseResponseDto.getCourseResultDto> mapped = courseList.stream()
                .map(course -> CourseResponseDto.getCourseResultDto.builder()
                        .title(course.getTitle())
                        .likeCount(course.getLikeCount())
                        .isLike(true)
                        .rampCount(course.getRampCount())
                        .stepCount(course.getStepCount())
                        .duration(course.getDuration())
                        .distance(course.getDistance())
                        .createdAt(course.getCreatedAt())
                        .rating(null)
                        .userType(null)
                        .build())
                .toList();

        return CourseResponseDto.getCourseListResultDto.builder()
                .courseList(mapped)
                .build();
    }

    public static CourseResponseDto.getCourseListResultDto getCourseResultDtoToGetCourseListResultDto(
            List<CourseResponseDto.getCourseResultDto> getCourseResultDtoList
    ) {
        return CourseResponseDto.getCourseListResultDto.builder()
                .courseList(getCourseResultDtoList)
                .build();
    }

    public static CourseResponseDto.GenerateResultDto toGenerateResultDto(List<Course> courseList) {
        return CourseResponseDto.GenerateResultDto.builder()
                .courses(courseList)
                .build();
    }

    public static CourseResponseDto.CourseDetailDto toCourseDetailDto(Course course, List<CoursePoint> points) {
        return CourseResponseDto.CourseDetailDto.builder()
                .id(course.getId())
                .userId(course.getUser().getId())
                .spotId(null)
                .title(course.getTitle())
                .stepCount(course.getStepCount())
                .rampCount(course.getRampCount())
                .likeCount(course.getLikeCount())
                .duration(course.getDuration())
                .distance(course.getDistance())
                .points(points.stream()
                        .map(p -> CourseResponseDto.PointDto.builder()
                                .seq(p.getSeq())
                                .latitude(p.getLatitude())
                                .longitude(p.getLongitude())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }
}
