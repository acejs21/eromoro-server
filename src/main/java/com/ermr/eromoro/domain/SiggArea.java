package com.ermr.eromoro.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "sigg_areas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SiggArea {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "sido_area_id")
    private SidoArea sidoArea;

    @Column(nullable = false, length = 20)
    private String name;

    private int code;

    /* 편의 메서드 */
    public void setSidoArea(SidoArea newSidoArea) {
        // 0) 동일 대상이면 아무 것도 안 함
        if (this.sidoArea == newSidoArea) return;
        // 1) 이전 관계 끊기: 기존 user 컬렉션에서 FeedbackList 제거
        if (this.sidoArea != null) {
            this.sidoArea.getSiggAreaList().remove(this);
        }
        // 2) 필드 교체
        this.sidoArea = newSidoArea;
        // 3) 새 관계에 추가 (null/중복 방지)
        if (newSidoArea != null && !newSidoArea.getSiggAreaList().contains(this)) {
            newSidoArea.getSiggAreaList().add(this);
        }
    }
}
