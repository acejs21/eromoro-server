package com.ermr.eromoro.dto;

import com.ermr.eromoro.domain.Feedback;
import com.ermr.eromoro.domain.enums.FeedbackType;
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
//        Long emdAreaId;
        String title;
        String content;
        String photoUrl;
        FeedbackType type;
        Boolean isReport;
        BigDecimal latitude;
        BigDecimal longitude;
        String address;
        int likeCount;
        int dislikeCount;
        boolean isLiked;
        LocalDateTime createdAt;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MyFeedbacksResultDto {
        List<MyFeedbackDto> feedbacks;
//        int totalCount;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MyFeedbackDto {
        Long feedbackId;
        //            Long emdAreaId;
        String photoUrl;
        String title;
        String address;
        FeedbackType type;
        LocalDateTime createdAt;
        int likeCount;
        int dislikeCount;
        Boolean isReport;
        Boolean accepted;
        boolean isLiked;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class updateLikeStatusResultDto {
        boolean isLiked;
        int likeCount;
    }
}
