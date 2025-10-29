package com.ermr.eromoro.dto;

import com.ermr.eromoro.domain.enums.FeedbackType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

public class BarrierResponseDto {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetAllBarrierDto {
        List<FeedbackDto> feedbacks;
        List<BarrierDto> barriers;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FeedbackDto {
        Long feedbackId;
        BigDecimal latitude;
        BigDecimal longitude;
        FeedbackType type;
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
}
