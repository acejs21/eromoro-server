package com.ermr.eromoro.converter;

import com.ermr.eromoro.domain.Course;
import com.ermr.eromoro.domain.CoursePoint;
import com.ermr.eromoro.domain.User;
import com.ermr.eromoro.domain.enums.CourseType;
import com.ermr.eromoro.dto.CourseResponseDto;

import java.util.List;

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

    public static CourseResponseDto.getCourseListResultDto toGetCourseListResultDto(
            List<Course> courseList,
            CourseType courseType
    ) {
        List<CourseResponseDto.getCourseResultDto> mapped = courseList.stream()
                .map(course -> CourseResponseDto.getCourseResultDto.builder()
                        .profileImage(course.getUser().getProfileImage()) // User 연관에서 프로필 이미지 추출 가정
                        .title(course.getTitle())
                        .likeCount(course.getLikeCount())
                        .rampCount(course.getRampCount())
                        .stepCount(course.getStepCount())
                        .duration(course.getDuration())
                        .distance(course.getDistance())
                        .createdAt(course.getCreatedAt())
                        .isPublic(courseType == CourseType.LIKE || courseType == CourseType.SPOT ? null : course.isPublic())
                        .build())
                .toList();

        return CourseResponseDto.getCourseListResultDto.builder()
                .courseList(mapped)
                .build();
    }
}
