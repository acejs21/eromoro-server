package com.ermr.eromoro.domain;

import com.ermr.eromoro.domain.enums.FacilityInfo;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "facility_information")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FacilityInformation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private FacilityInfo facilityInfo;
}
