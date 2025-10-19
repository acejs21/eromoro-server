package com.ermr.eromoro.service;

import com.ermr.eromoro.domain.Course;
import com.ermr.eromoro.domain.User;
import com.ermr.eromoro.dto.CourseRequestDto;
import com.ermr.eromoro.dto.CourseResponseDto;

import java.util.List;

public interface CourseCommandService {

    void startCourse(Long userId);
    User getIsCourseStatus(Long userId);
    Course saveCoursePointList(Long userId, CourseRequestDto.CoursePointListDto request);
    void saveCourseInfo(Long userId, Long courseId, CourseRequestDto.CourseInfoDto request);
}

