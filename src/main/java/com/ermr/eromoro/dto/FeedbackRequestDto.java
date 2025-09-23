package com.ermr.eromoro.dto;

import com.ermr.eromoro.domain.Feedback;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.math.BigDecimal;

public class FeedbackRequestDto {

    @Getter
    public static class CreateDto {
        BigDecimal latitude;
        BigDecimal longitude;
        String title;
        String content;
        Feedback.FeedbackType type;

        @JsonProperty("isReport")
        boolean isReport;
        String photo;
    }

    @Getter
    public static class UpdateDto {
        BigDecimal latitude;
        BigDecimal longitude;
        String title;
        String content;
        Feedback.FeedbackType type;

        @JsonProperty("isReport")
        boolean isReport;
    }
}
