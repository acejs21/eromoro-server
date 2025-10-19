package com.ermr.eromoro.converter;

import com.ermr.eromoro.domain.Course;
import com.ermr.eromoro.domain.CoursePoint;
import com.ermr.eromoro.domain.User;
import com.ermr.eromoro.dto.CourseRequestDto;
import com.ermr.eromoro.dto.CourseResponseDto;

import java.util.List;

public class CoursePointConverter {

    public static List<CoursePoint> toCoursePointList (CourseRequestDto.CoursePointListDto request, Course course) {
        return request.getCoursePointList().stream()
                .map(coursePoint -> CoursePoint.builder()
                        .course(course)
                        .latitude(coursePoint.getLatitude())
                        .longitude(coursePoint.getLongitude())
                        .build())
                .toList();
    }
}
