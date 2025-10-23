package com.ermr.eromoro.repository;

import com.ermr.eromoro.domain.Feedback;
import com.ermr.eromoro.domain.LikeCourse;
import com.ermr.eromoro.domain.LikeFeedback;
import com.ermr.eromoro.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LikeFeedbackRepository extends JpaRepository<LikeFeedback, Long> {
    LikeFeedback findByUserAndFeedback(User getUser, Feedback getFeedback);
}
