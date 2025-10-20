package com.ermr.eromoro.service;

import com.ermr.eromoro.domain.Course;
import com.ermr.eromoro.domain.enums.CourseType;
import com.ermr.eromoro.dto.CourseResponseDto;

import java.util.List;

public interface CourseQueryService {
    CourseResponseDto.getCourseListResultDto getCourseList(Long userId, CourseType courseType, String keyword, Long spotId);
}
