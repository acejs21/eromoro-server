package com.ermr.eromoro.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "spot_facility_information")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SpotFacilityInformation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "spot_id")
    private Spot spot;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "facility_information_id")
    private FacilityInformation facilityInformation;

    /* 편의 메서드 */
    public void setSpot(Spot newSpot) {
        // 0) 동일 대상이면 아무 것도 안 함
        if (this.spot == newSpot) return;
        // 1) 이전 관계 끊기: 기존 user 컬렉션에서 FeedbackList 제거
        if (this.spot != null) {
            this.spot.getSpotFacilityInformationList().remove(this);
        }
        // 2) 필드 교체
        this.spot = newSpot;
        // 3) 새 관계에 추가 (null/중복 방지)
        if (newSpot != null && !newSpot.getSpotFacilityInformationList().contains(this)) {
            newSpot.getSpotFacilityInformationList().add(this);
        }
    }

    public void setFacilityInformation(FacilityInformation facilityInformation) {
        this.facilityInformation = facilityInformation;
    }
}
