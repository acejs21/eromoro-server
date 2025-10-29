package com.ermr.eromoro.dto;

import com.ermr.eromoro.domain.enums.FacilityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public class SpotResponseDto {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetSpotListDto {
        List<SpotDto> spotList;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SpotDto {
        private Long spotId;
        private String name;
        private String imageUrl;
        private int courseCount;
        private UserTypeDto userType;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserTypeDto {
        private boolean isDisabled;
        private boolean isSenior;
        private boolean isPregnant;
        private boolean isInfantGuardian;
        private boolean isUser;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetSpotDetailDto {
        private Long spotId;
        private String title;
        private String information;
        private String imageUrl;
        private List<FacilityInfo> facilityInfo;
        private String address;
    }
}
