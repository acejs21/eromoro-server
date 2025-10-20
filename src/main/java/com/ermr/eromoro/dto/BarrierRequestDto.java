package com.ermr.eromoro.dto;

import lombok.Getter;

import java.math.BigDecimal;

public class BarrierRequestDto {

    @Getter
    public static class StepDataPhotoMetaDataDto {
        String panoId;
        BigDecimal latitude;
        BigDecimal longitude;
    }
}
