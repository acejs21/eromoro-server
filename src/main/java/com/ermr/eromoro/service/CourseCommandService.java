package com.ermr.eromoro.service;

import com.ermr.eromoro.domain.Course;
import com.ermr.eromoro.domain.User;
import com.ermr.eromoro.dto.CourseRequestDto;
import com.ermr.eromoro.dto.CourseResponseDto;

public interface CourseCommandService {

    void startCourse(Long userId);
    User getIsCourseStatus(Long userId);
    Course saveCoursePointList(Long userId, Long spotId, CourseRequestDto.CoursePointListDto request);
    void saveCourseInfo(Long userId, Long courseId, CourseRequestDto.CourseInfoDto request);
    CourseResponseDto.updateLikeStatusResultDto updateLikeStatus(Long userId, Long courseId, boolean like);
    void deleteCourse(Long userId, Long courseId);
}

