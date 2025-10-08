package com.ermr.eromoro.service;

import com.ermr.eromoro.apiResponse.exception.handler.FeedbackHandler;
import com.ermr.eromoro.apiResponse.status.ErrorStatus;
import com.ermr.eromoro.domain.Feedback;
import com.ermr.eromoro.repository.FeedbackRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedbackQueryServiceImpl implements FeedbackQueryService {

    private final FeedbackRepository feedbackRepository;

    @Override
    public Feedback findById(Long feedbackId) {
        return feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new FeedbackHandler(ErrorStatus.FEEDBACK_NOT_FOUND));
    }

    @Override
    public List<Feedback> getMyFeedbacks(Long userId) {
        return feedbackRepository.findAllByUserId(userId);
    }
}
