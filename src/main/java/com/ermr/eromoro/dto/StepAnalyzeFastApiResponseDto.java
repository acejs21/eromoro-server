package com.ermr.eromoro.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;

public class StepAnalyzeFastApiResponseDto {

    @Getter
    public static class AnalyzeResponse {
        @JsonProperty("stair_ids")
        private List<String> stairIds;
        private int total;
    }
}
