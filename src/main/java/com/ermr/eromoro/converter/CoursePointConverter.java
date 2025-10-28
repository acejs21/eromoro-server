package com.ermr.eromoro.converter;

import com.ermr.eromoro.domain.Course;
import com.ermr.eromoro.domain.CoursePoint;
import com.ermr.eromoro.domain.User;
import com.ermr.eromoro.dto.CourseRequestDto;
import com.ermr.eromoro.dto.CourseResponseDto;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CoursePointConverter {

    public static List<CoursePoint> toCoursePointList (CourseRequestDto.CoursePointListDto request, Course course) {
        return IntStream.range(0, request.getCoursePointList().size())
                .mapToObj(i -> {
                    CourseRequestDto.CoursePointDto coursePointDto = request.getCoursePointList().get(i);
                    return CoursePoint.builder()
                            .course(course)
                            .latitude(coursePointDto.getLatitude())
                            .longitude(coursePointDto.getLongitude())
                            .seq(i) // 스트림의 인덱스를 seq 값으로 설정
                            .build();
                })
                .collect(Collectors.toList());
    }
}
