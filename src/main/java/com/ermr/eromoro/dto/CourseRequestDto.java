package com.ermr.eromoro.dto;

import com.ermr.eromoro.domain.enums.FeedbackType;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import lombok.Getter;

import java.math.BigDecimal;
import java.sql.Time;
import java.time.LocalTime;
import java.util.List;

public class CourseRequestDto {

    @Getter
    public static class CoursePointListDto {
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
        @Schema(type = "string", format = "time", example = "16:02:09", pattern = "HH:mm:ss", description = "시:분:초")
        LocalTime duration;
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
}
