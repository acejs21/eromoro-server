package com.ermr.eromoro.service;

import com.ermr.eromoro.apiResponse.exception.handler.FeedbackHandler;
import com.ermr.eromoro.apiResponse.status.ErrorStatus;
import com.ermr.eromoro.converter.FeedbackConverter;
import com.ermr.eromoro.domain.Feedback;
import com.ermr.eromoro.dto.FeedbackRequestDto;
import com.ermr.eromoro.repository.FeedbackRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;

    @Transactional
    public Feedback create(FeedbackRequestDto.CreateDto request) {
        Feedback newFeedback = FeedbackConverter.toFeedback(request);

        // 임시로 하드코드 userId 할당
        // 헤더의 accessToken에서 userId 값 추출 필요
        newFeedback.setUserId(1L);
        return feedbackRepository.save(newFeedback);
    }

    @Transactional
    public Feedback findById(Long feedbackId) {
        return feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new EntityNotFoundException("Feedback not found: " + feedbackId));
    }

    @Transactional
    public List<Feedback> getMyFeedbacks(Long userId) {
        return feedbackRepository.findAllByUserId(userId);
    }

    @Transactional
    public Feedback update(Long feedbackId, FeedbackRequestDto.UpdateDto request) {
        Feedback feedback = feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new EntityNotFoundException("Feedback not found: " + feedbackId));

        if (request.getTitle() != null) { feedback.setTitle(request.getTitle()); }
        if (request.getContent() != null) { feedback.setContent(request.getContent()); }
        if (request.getType() != null) { feedback.setType(request.getType()); }
        if (request.isReport()) { feedback.setReport(true); }
        if (request.getLatitude() != null) { feedback.setLatitude(request.getLatitude()); }
        if (request.getLongitude() != null) { feedback.setLongitude(request.getLongitude()); }

        return feedbackRepository.save(feedback);
    }

    @Transactional
    public LocalDateTime delete(Long feedbackId) {
        Feedback feedback = feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new FeedbackHandler(ErrorStatus.FEEDBACK_NOT_FOUND));
        // 권한 없음 에러 추가

        feedbackRepository.delete(feedback);
        return LocalDateTime.now();
    }
}
