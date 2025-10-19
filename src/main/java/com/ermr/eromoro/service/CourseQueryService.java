package com.ermr.eromoro.service;

import com.ermr.eromoro.domain.Course;
import com.ermr.eromoro.domain.enums.CourseType;

import java.util.List;

public interface CourseQueryService {
    List<Course> getCourseList(Long userId, CourseType courseType, String keyword, Long spotId);
}
