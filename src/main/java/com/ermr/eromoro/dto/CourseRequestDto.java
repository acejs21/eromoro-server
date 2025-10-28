package com.ermr.eromoro.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;

public class CourseRequestDto {

    @Getter
    public static class CoursePointListDto {
        int duration;
        float distance;
        List<CoursePointDto> coursePointList;
    }

    @Getter
    public static class CoursePointDto {
        BigDecimal latitude;
        BigDecimal longitude;
    }

    @Getter
    public static class CourseInfoDto {
        String title;
        Boolean isPublic;
        int rating;
    }

    @Getter
    public static class GenerateDto {
        LatLon start;
        LatLon end;
        Integer targetDurationMin;
        BigDecimal speedMps;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class LatLon {
        BigDecimal lat;
        BigDecimal lon;
    }
}
