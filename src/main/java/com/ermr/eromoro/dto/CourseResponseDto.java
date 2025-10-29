package com.ermr.eromoro.dto;

import com.ermr.eromoro.domain.enums.UserType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
        Long courseId;
        String title;
        int likeCount;
        boolean isLiked;
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
        List<CourseSimpleDto> courses;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CourseSimpleDto {
        private Long id;
        private String title;
        private int duration;     // 분
        private float distance;   // km (소수 1자리)
        private List<GeneratePointDto> points;  // 경로 좌표들 (seq 순)

        @Getter
        @AllArgsConstructor
        public static class GeneratePointDto {
            private int seq;
            private BigDecimal lat;
            private BigDecimal lon;
            private String slope;
        }
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
        List<CourseDetailPointDto> points;
//        List<FeedbackDto> feedbacks;
//        List<BarrierDto> barriers;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CourseDetailPointDto {
        int seq;
        BigDecimal latitude;
        BigDecimal longitude;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FeedbackDto {
        Long feedbackId;
        BigDecimal latitude;
        BigDecimal longitude;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BarrierDto {
        Long barrierId;
        String imageUrl;
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
