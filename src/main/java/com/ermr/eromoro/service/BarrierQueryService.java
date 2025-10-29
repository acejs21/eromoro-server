package com.ermr.eromoro.service;

import com.ermr.eromoro.domain.Barrier;
import com.ermr.eromoro.domain.Feedback;
import com.ermr.eromoro.dto.BarrierRequestDto;

import java.util.List;

public interface BarrierQueryService {
    List<Feedback> getAllFeedbacks();
    List<Barrier> getAllBarriers();
    List<Feedback> getPagingFeedbacks(BarrierRequestDto.GetPagingBarrierDto request);
    List<Barrier> getPagingBarriers(BarrierRequestDto.GetPagingBarrierDto request);
    void checkAuthorization(Long userId);
}
