package com.ermr.eromoro.controller;

import com.ermr.eromoro.apiResponse.ApiResponse;
import com.ermr.eromoro.converter.CourseConverter;
import com.ermr.eromoro.domain.Course;
import com.ermr.eromoro.domain.CoursePoint;
import com.ermr.eromoro.dto.CourseRequestDto;
import com.ermr.eromoro.dto.CourseResponseDto;
import com.ermr.eromoro.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/courses")
public class CourseController {

    private final CourseService courseService;

    @PostMapping("/generate")
    public ApiResponse<CourseResponseDto.GenerateResultDto> generate(
            @RequestBody CourseRequestDto.GenerateDto request
    ){
        List<Course> courseList = courseService.generateCourse(request);
        return ApiResponse.onSuccess(CourseConverter.toGenerateResultDto(courseList));
    }

    @GetMapping("/{course_id}")
    public ApiResponse<CourseResponseDto.CourseDetailDto> getCourseDetail(
            @PathVariable("course_id") Long courseId
    ) {
        Course course = courseService.getCourse(courseId);
        List<CoursePoint> points = courseService.getCoursePointsOrdered(courseId);
        return ApiResponse.onSuccess(CourseConverter.toCourseDetailDto(course, points));
    }
}
