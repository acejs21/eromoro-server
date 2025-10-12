package com.ermr.eromoro.converter;

import com.ermr.eromoro.domain.Course;
import com.ermr.eromoro.domain.CoursePoint;
import com.ermr.eromoro.dto.CourseResponseDto;

import java.util.List;
import java.util.stream.Collectors;

public class CourseConverter {

    public static CourseResponseDto.GenerateResultDto toGenerateResultDto(List<Course> courseList) {
        return CourseResponseDto.GenerateResultDto.builder()
                .courses(courseList)
                .build();
    }

    public static CourseResponseDto.CourseDetailDto toCourseDetailDto(Course course, List<CoursePoint> points) {
        return CourseResponseDto.CourseDetailDto.builder()
                .id(course.getId())
                .userId(course.getUserId())
                .spotId(course.getSpotId())
                .name(course.getName())
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
