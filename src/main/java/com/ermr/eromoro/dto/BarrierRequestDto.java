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

    @Getter
    public static class GetPagingBarrierDto {
        CoordinateDto sw = new CoordinateDto();
        CoordinateDto se = new CoordinateDto();
        CoordinateDto ne = new CoordinateDto();
        CoordinateDto nw = new CoordinateDto();
    }

    @Getter
    public static class CoordinateDto {
        BigDecimal latitude;
        BigDecimal longitude;
    }
}
