package com.ermr.eromoro.converter;

import com.ermr.eromoro.domain.FacilityInformation;
import com.ermr.eromoro.domain.Spot;
import com.ermr.eromoro.domain.SpotFacilityInformation;
import com.ermr.eromoro.domain.enums.FacilityInfo;
import com.ermr.eromoro.dto.SpotResponseDto;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

public class SpotConverter {

    public static SpotResponseDto.GetSpotListDto toGetSpotListDto(Page<SpotResponseDto.SpotDto> spotPage) {
        return SpotResponseDto.GetSpotListDto.builder()
                .spotList(spotPage.getContent())
                .build();
    }

    public static SpotResponseDto.GetSpotDetailDto toGetSpotDetailDto(Spot getSpot, List<SpotFacilityInformation> facilityInformationList) {
        List<FacilityInfo> facilityInfoList = facilityInformationList.stream()
                .map(SpotFacilityInformation::getFacilityInformation)
                .map(FacilityInformation::getFacilityInfo)
                .collect(Collectors.toList());

        return SpotResponseDto.GetSpotDetailDto.builder()
                .spotId(getSpot.getId())
                .title(getSpot.getTitle())
                .information(getSpot.getInformation())
                .imageUrl(getSpot.getImageUrl().equals("") ? null : getSpot.getImageUrl())
                .facilityInfo(facilityInfoList)
                .build();
    }
}
