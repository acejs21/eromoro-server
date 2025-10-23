package com.ermr.eromoro.service;

import com.ermr.eromoro.domain.Feedback;
import com.ermr.eromoro.dto.FeedbackResponseDto;

import java.util.List;

public interface FeedbackQueryService {

    FeedbackResponseDto.DetailResultDto getDetailFeedback(Long userId, Long feedbackId);
    FeedbackResponseDto.MyFeedbacksResultDto getMyFeedbacks(Long userId);
}
