package com.ermr.eromoro.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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

    @Column(name = "user_id",  nullable = false)
    private Long userId;

//    /** FK: users.id */
//    @ManyToOne(fetch = FetchType.LAZY, optional = false)
//    @JoinColumn(name = "user_id", nullable = false)
//    private User user;

//    @Column(name = "emd_area_id", nullable = false)
    @Column(name = "emd_area_id")
    private Long emdAreaId;

    @Column(name = "title", columnDefinition = "TEXT", nullable = false)
    private String title;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    /** 이미지 경로/URL 등 */
    @Column(name = "photo", columnDefinition = "TEXT")
    private String photo;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private FeedbackType type;

    @Column(name = "is_report", nullable = false)
    private boolean isReport;

    @Column(name = "latitude", precision = 10, scale = 6, nullable = false)
    private BigDecimal latitude;

    @Column(name = "longitude", precision = 10, scale = 6, nullable = false)
    private BigDecimal longitude;

    @Column(name = "like_count", nullable = false)
    private int likeCount;

    @Column(name = "dislike_count", nullable = false)
    private int dislikeCount;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public enum FeedbackType {
        STAIR,
        ELEVATOR,
        OTHER
    }

    /* 편의 메서드 */
    public void like() { this.likeCount++; }
    public void dislike() { this.dislikeCount++; }
    public void cancelLike() { if (this.likeCount > 0) this.likeCount--; }
    public void cancelDislike() { if (this.dislikeCount > 0) this.dislikeCount--; }
}