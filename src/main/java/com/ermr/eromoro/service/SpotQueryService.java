package com.ermr.eromoro.service;

import com.ermr.eromoro.domain.enums.ContentType;
import com.ermr.eromoro.dto.SpotResponseDto;

public interface SpotQueryService {
    SpotResponseDto.GetSpotListDto getSpotList(ContentType category, String siGunGu, String keyword, int pageNum);
    SpotResponseDto.GetSpotDetailDto getSpotDetail(Long userId, Long spotId);
}
