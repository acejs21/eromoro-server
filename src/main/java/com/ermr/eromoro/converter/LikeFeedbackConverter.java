package com.ermr.eromoro.converter;

import com.ermr.eromoro.domain.Feedback;
import com.ermr.eromoro.domain.LikeFeedback;
import com.ermr.eromoro.domain.User;

public class LikeFeedbackConverter {
    public static LikeFeedback toLikeFeedback(User getUser, Feedback getFeedback) {
        return LikeFeedback.builder()
                .user(getUser)
                .feedback(getFeedback)
                .build();
    }
}
