package com.ermr.eromoro.service;

import com.ermr.eromoro.apiResponse.exception.handler.FeedbackHandler;
import com.ermr.eromoro.apiResponse.exception.handler.UserHandler;
import com.ermr.eromoro.apiResponse.status.ErrorStatus;
import com.ermr.eromoro.converter.FeedbackConverter;
import com.ermr.eromoro.domain.Feedback;
import com.ermr.eromoro.domain.User;
import com.ermr.eromoro.dto.FeedbackRequestDto;
import com.ermr.eromoro.repository.FeedbackRepository;
import com.ermr.eromoro.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedbackCommandServiceImpl implements FeedbackCommandService {

    private final FeedbackRepository feedbackRepository;
    private final UserRepository userRepository;

    @Override
    public Feedback create(FeedbackRequestDto.CreateDto request, Long userId) {
        User getUser = userRepository.findById(userId) // 후에 null처리하기 (2025/10/06)
                .orElseThrow(() -> new UserHandler(ErrorStatus.USER_NOT_FOUND));
        Feedback newFeedback = FeedbackConverter.toFeedback(request);

        newFeedback.setUser(getUser);
        return feedbackRepository.save(newFeedback);
    }

    @Override
    @Transactional
    public Feedback update(Long feedbackId, FeedbackRequestDto.UpdateDto request) {
        Feedback feedback = feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new EntityNotFoundException("Feedback not found: " + feedbackId));

        if (request.getTitle() != null) { feedback.setTitle(request.getTitle()); }
        if (request.getContent() != null) { feedback.setContent(request.getContent()); }
        if (request.getType() != null) { feedback.setType(request.getType()); }
        if (request.getIsReport()) { feedback.setIsReport(true); }
        if (request.getLatitude() != null) { feedback.setLatitude(request.getLatitude()); }
        if (request.getLongitude() != null) { feedback.setLongitude(request.getLongitude()); }

        return feedbackRepository.save(feedback);
    }

    @Override
    public LocalDateTime delete(Long feedbackId) {
        Feedback feedback = feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new FeedbackHandler(ErrorStatus.FEEDBACK_NOT_FOUND));
        // 권한 없음 에러 추가

        feedbackRepository.delete(feedback);
        return LocalDateTime.now();
    }
}
