package com.ermr.eromoro.service;

import com.ermr.eromoro.apiResponse.exception.handler.FeedbackHandler;
import com.ermr.eromoro.apiResponse.exception.handler.UserHandler;
import com.ermr.eromoro.apiResponse.status.ErrorStatus;
import com.ermr.eromoro.converter.FeedbackConverter;
import com.ermr.eromoro.domain.Feedback;
import com.ermr.eromoro.domain.LikeFeedback;
import com.ermr.eromoro.domain.User;
import com.ermr.eromoro.dto.FeedbackResponseDto;
import com.ermr.eromoro.repository.FeedbackRepository;
import com.ermr.eromoro.repository.LikeFeedbackRepository;
import com.ermr.eromoro.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FeedbackQueryServiceImpl implements FeedbackQueryService {

    private final UserRepository userRepository;
    private final FeedbackRepository feedbackRepository;
    private final LikeFeedbackRepository likeFeedbackRepository;

    @Override
    public FeedbackResponseDto.DetailResultDto getDetailFeedback(Long userId, Long feedbackId) {
        User getUser = userRepository.findById(userId)
                .orElseThrow(() -> new UserHandler(ErrorStatus.USER_NOT_FOUND));
        Feedback getFeedback = feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new FeedbackHandler(ErrorStatus.FEEDBACK_NOT_FOUND));
        boolean isLiked;

        if(!getFeedback.getUser().equals(getUser)) {
            throw new FeedbackHandler(ErrorStatus.FEEDBACK_NOT_AUTHORIZED);
        }
        isLiked = getFeedbackisLiked(getUser, getFeedback);

        return FeedbackConverter.toDetailResultDto(getFeedback, isLiked);
    }

    @Override
    public FeedbackResponseDto.MyFeedbacksResultDto getMyFeedbacks(Long userId) {
        User getUser = userRepository.findById(userId)
                .orElseThrow(() -> new UserHandler(ErrorStatus.USER_NOT_FOUND));

        return FeedbackConverter.toMyFeedbacksResultDto(feedbackRepository.findAllByUserIdWithLikeFeedback(userId));
    }

    private boolean getFeedbackisLiked(User user, Feedback feedback) {
        LikeFeedback getLikeFeedback = likeFeedbackRepository.findByUserAndFeedback(user, feedback);
        if (getLikeFeedback != null) return false;
        else return true;
    }
}
