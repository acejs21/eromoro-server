package com.ermr.eromoro.dto;

import com.ermr.eromoro.domain.enums.FeedbackType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class CourseResponseDto {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class isCourseResultDto {
        boolean isCourse;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class saveCoursePointResultDto {
        Long courseId;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class getCourseListResultDto {
        List<getCourseResultDto> courseList;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class getCourseResultDto {
        String profileImage;
        String title;
        int likeCount;
        int rampCount;
        int stepCount;
        LocalTime duration;
        float distance;
        Boolean isPublic;
        LocalDateTime createdAt;
    }
}
