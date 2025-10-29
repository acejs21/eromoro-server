package com.ermr.eromoro.converter;

import com.ermr.eromoro.domain.*;
import com.ermr.eromoro.dto.CourseResponseDto;

import java.util.Comparator;
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
                        .courseId(course.getId())
                        .title(course.getTitle())
                        .likeCount(course.getLikeCount())
                        .isLiked(true)
                        .rampCount(course.getRampCount())
                        .stepCount(course.getStepCount())
                        .duration(course.getDuration())
                        .distance(course.getDistance())
                        .createdAt(course.getCreatedAt())
                        .rating(null)
                        .userType(null)
                        .photo(course.getSpot() != null ? course.getSpot().getImageUrl() : null)
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
        List<CourseResponseDto.CourseSimpleDto> dtos = courseList.stream()
                .map(CourseConverter::toCourseSimpleDto)
                .toList();

        return CourseResponseDto.GenerateResultDto.builder()
                .courses(dtos)
                .build();
    }

    private static CourseResponseDto.CourseSimpleDto toCourseSimpleDto(Course c) {
        // 포인트 컬렉션 이름/타입은 프로젝트에 맞게 조정하세요.
        // 예: c.getPoints() 또는 c.getCoursePoints()
        List<CoursePoint> points = c.getCoursePointList(); // ← 필요 시 getter 명 수정
        List<CourseResponseDto.CourseSimpleDto.GeneratePointDto> geometry = points.stream()
                .sorted(Comparator.comparingInt(CoursePoint::getSeq))
                .map(p -> new CourseResponseDto.CourseSimpleDto.GeneratePointDto(
                        p.getSeq(), p.getLatitude(), p.getLongitude(), p.getSlope()
                ))
                .toList();

        return CourseResponseDto.CourseSimpleDto.builder()
                .id(c.getId())
                .title(c.getTitle())
                .duration(c.getDuration())
                .distance(c.getDistance())
                .points(geometry)
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
                        .map(p -> CourseResponseDto.CourseDetailPointDto.builder()
                                .seq(p.getSeq())
                                .latitude(p.getLatitude())
                                .longitude(p.getLongitude())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }

    public static CourseResponseDto.updateLikeStatusResultDto toUpdateLikeStatusResultDto(boolean isLiked, int likeCount) {
        return CourseResponseDto.updateLikeStatusResultDto.builder()
                .isLiked(isLiked)
                .likeCount(likeCount)
                .build();
    }
}
