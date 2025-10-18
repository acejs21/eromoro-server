package com.ermr.eromoro.dto;

import com.ermr.eromoro.domain.Course;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

public class CourseResponseDto {

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
}
