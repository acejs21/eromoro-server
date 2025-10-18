package com.ermr.eromoro.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

public class CourseRequestDto {

    @Getter
    public static class GenerateDto {
        LatLon start;
        LatLon end;
        Integer targetDurationMin;
        BigDecimal speedMps;
    }

    @Getter
    @Setter
    public static class LatLon {
        BigDecimal lat;
        BigDecimal lon;
    }
}
