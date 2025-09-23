package com.ermr.eromoro.repository;

import com.ermr.eromoro.domain.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    List<Feedback> findAllByUserId(Long userId);
}
