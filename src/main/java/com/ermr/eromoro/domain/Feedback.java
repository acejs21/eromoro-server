package com.ermr.eromoro.domain;

import com.ermr.eromoro.domain.enums.FeedbackType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "feedbacks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "emd_area_id")
    private Long emdAreaId;

    @Column(name = "title", columnDefinition = "TEXT", nullable = false)
    private String title;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    /** 이미지 경로/URL 등 */
    @Column(name = "photo_url", columnDefinition = "TEXT")
    private String photoUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private FeedbackType type;

    @Column(name = "is_report", nullable = false)
    private Boolean isReport;

    @Column(name = "accepted", nullable = false) // false로 초기화
    private Boolean accepted;

    @Column(name = "latitude", precision = 10, scale = 6, nullable = false)
    private BigDecimal latitude;

    @Column(name = "longitude", precision = 10, scale = 6, nullable = false)
    private BigDecimal longitude;

    @Column(name = "address", length = 200, nullable = false)
    private String address;

    @Column(name = "like_count", nullable = false)
    private int likeCount;

    @Column(name = "dislike_count", nullable = false)
    private int dislikeCount;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "feedback", cascade = CascadeType.ALL)
    private List<LikeFeedback> likeFeedbackList = new ArrayList<>();

    /* 편의 메서드 */
    public void setUser(User newUser) {
        // 0) 동일 대상이면 아무 것도 안 함
        if (this.user == newUser) return;
        // 1) 이전 관계 끊기: 기존 user 컬렉션에서 FeedbackList 제거
        if (this.user != null) {
            this.user.getFeedbackList().remove(this);
        }
        // 2) 필드 교체
        this.user = newUser;
        // 3) 새 관계에 추가 (null/중복 방지)
        if (newUser != null && !newUser.getFeedbackList().contains(this)) {
            newUser.getFeedbackList().add(this);
        }
    }
    public void like() { this.likeCount++; }
    public void dislike() { this.dislikeCount++; }
    public void cancelLike() { if (this.likeCount > 0) this.likeCount--; }
    public void cancelDislike() { if (this.dislikeCount > 0) this.dislikeCount--; }

    public void updateLikeCount(int likeCountDelta) {
        this.likeCount += likeCountDelta;
    }
}