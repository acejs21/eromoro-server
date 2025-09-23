package com.ermr.eromoro.dto;

import com.ermr.eromoro.domain.Feedback;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class FeedbackResponseDto {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateResultDto {
        Long feedbackId;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DetailResultDto {
        Long feedbackId;
        Long userId;
        Long emdAreaId;
        String title;
        String content;
        Feedback.FeedbackType type;
        boolean isReport;
        BigDecimal latitude;
        BigDecimal longitude;
        int likeCount;
        int dislikeCount;
        LocalDateTime createdAt;
        LocalDateTime updatedAt;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MyFeedbacksResultDto {
        List<MyFeedbackDto> feedbacks;
        int totalCount;

        @Builder
        @Getter
        @NoArgsConstructor
        @AllArgsConstructor
        public static class MyFeedbackDto {
            Long feedbackId;
            boolean isReport;
            Long emdAreaId;
            String title;
            Feedback.FeedbackType type;
            LocalDateTime updatedAt;
            int likeCount;
            int dislikeCount;
        }
    }
}
