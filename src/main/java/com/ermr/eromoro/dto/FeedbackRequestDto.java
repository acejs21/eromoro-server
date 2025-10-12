package com.ermr.eromoro.dto;

import com.ermr.eromoro.domain.enums.FeedbackType;
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
        FeedbackType type;
        String address;
        @JsonProperty("isReport")
        Boolean isReport;
    }

    @Getter
    public static class UpdateDto {
        BigDecimal latitude;
        BigDecimal longitude;
        String title;
        String content;
        FeedbackType type;
        @JsonProperty("isReport")
        Boolean isReport;
    }
}
