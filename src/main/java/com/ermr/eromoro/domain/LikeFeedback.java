package com.ermr.eromoro.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "like_feedbacks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LikeFeedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "feedback_id")
    private Feedback feedback;

    /* 편의 메서드: User 교체 */
    public void setUser(User newUser) {
        // 0) 동일 대상이면 아무 것도 안 함
        if (this.user == newUser) return;
        // 1) 이전 관계 끊기
        if (this.user != null) {
            this.user.getLikeFeedbackList().remove(this);
        }
        // 2) 필드 교체
        this.user = newUser;
        // 3) 새 관계에 추가 (null/중복 방지)
        if (newUser != null && !newUser.getLikeFeedbackList().contains(this)) {
            newUser.getLikeFeedbackList().add(this);
        }
    }

    /* 편의 메서드: Feedback 교체 */
    public void setFeedback(Feedback newFeedback) {
        // 0) 동일 대상이면 아무 것도 안 함
        if (this.feedback == newFeedback) return;
        // 1) 이전 관계 끊기
        if (this.feedback != null) {
            this.feedback.getLikeFeedbackList().remove(this);
        }
        // 2) 필드 교체
        this.feedback = newFeedback;
        // 3) 새 관계에 추가 (null/중복 방지)
        if (newFeedback != null && !newFeedback.getLikeFeedbackList().contains(this)) {
            newFeedback.getLikeFeedbackList().add(this);
        }
    }

    /* 두 연관관계를 한 번에 설정하는 편의 메서드*/
    public void connect(User user, Feedback feedback) {
        setUser(user);
        setFeedback(feedback);
    }
}
