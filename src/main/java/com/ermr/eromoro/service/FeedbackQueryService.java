package com.ermr.eromoro.service;

import com.ermr.eromoro.domain.Feedback;

import java.util.List;

public interface FeedbackQueryService {

    Feedback findById(Long feedbackId);
    List<Feedback> getMyFeedbacks(Long userId);
}
