package com.ermr.eromoro.service;

import com.ermr.eromoro.apiResponse.exception.handler.UserHandler;
import com.ermr.eromoro.apiResponse.status.ErrorStatus;
import com.ermr.eromoro.converter.SpotConverter;
import com.ermr.eromoro.domain.Spot;
import com.ermr.eromoro.domain.SpotFacilityInformation;
import com.ermr.eromoro.domain.User;
import com.ermr.eromoro.domain.enums.ContentType;
import com.ermr.eromoro.dto.SpotResponseDto;
import com.ermr.eromoro.repository.SpotFacilityInformationRepository;
import com.ermr.eromoro.repository.SpotRepository;
import com.ermr.eromoro.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SpotQueryServiceImpl implements SpotQueryService {

    private final SpotRepository spotRepository;
    private final UserRepository userRepository;
    private final SpotFacilityInformationRepository spotFacilityInformationRepository;

    @Override
    public SpotResponseDto.GetSpotListDto getSpotList(ContentType category, String siGunGu, String keyword, int pageNum) {
        Page<SpotResponseDto.SpotDto> spotDtoList = spotRepository.findSpotsWithUserTypes(category, siGunGu, keyword, PageRequest.of(pageNum, 5));
        return SpotConverter.toGetSpotListDto(spotDtoList);
    }

    @Override
    public SpotResponseDto.GetSpotDetailDto getSpotDetail(Long userId, Long spotId) {
        User getUser = userRepository.findById(userId)
                .orElseThrow(() -> new UserHandler(ErrorStatus.USER_NOT_FOUND));
        Spot getSpot = spotRepository.findById(spotId)
                .orElseThrow(() -> new UserHandler(ErrorStatus.SPOT_NOT_FOUND));
        List<SpotFacilityInformation> facilityInformationList = spotFacilityInformationRepository.findBySpot(getSpot);

        return SpotConverter.toGetSpotDetailDto(getSpot, facilityInformationList);
    }
}
