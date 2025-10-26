package com.ermr.eromoro.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "spot_images")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SpotImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "spot_id")
    private Spot spot;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String imageUrl;

    /* 편의 메서드 */
    public void setSpot(Spot newSpot) {
        // 0) 동일 대상이면 아무 것도 안 함
        if (this.spot == newSpot) return;
        // 1) 이전 관계 끊기: 기존 user 컬렉션에서 FeedbackList 제거
        if (this.spot != null) {
            this.spot.getSpotImageList().remove(this);
        }
        // 2) 필드 교체
        this.spot = newSpot;
        // 3) 새 관계에 추가 (null/중복 방지)
        if (newSpot != null && !newSpot.getSpotImageList().contains(this)) {
            newSpot.getSpotImageList().add(this);
        }
    }
}
