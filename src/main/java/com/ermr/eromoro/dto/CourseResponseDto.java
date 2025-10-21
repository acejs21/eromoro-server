package com.ermr.eromoro.dto;

import com.ermr.eromoro.domain.Course;
import com.ermr.eromoro.domain.enums.UserType;
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
        String title;
        int likeCount;
        boolean isLike;
        int rampCount;
        int stepCount;
        int duration;
        float distance;
        LocalDateTime createdAt;
        Integer rating;
        UserType userType;
        String photo;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GenerateResultDto {
        List<Course> courses;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CourseDetailDto {
        Long id;
        Long userId;
        Long spotId;
        String title;
        int stepCount;
        int rampCount;
        int likeCount;
        int duration;
        float distance;
        List<PointDto> points;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PointDto {
        int seq;
        BigDecimal latitude;
        BigDecimal longitude;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class updateLikeStatusResultDto {
        boolean isLiked;
        int likeCount;
    }
}
