package com.ermr.eromoro.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "spots")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Spot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "sigg_area_id")
    private SiggArea siggArea;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String information;

    @Column(columnDefinition = "TEXT")
    private String address;

    public void setSiggArea(SiggArea siggArea) {
        this.siggArea = siggArea;
    }

    @OneToMany(mappedBy = "spot", cascade = CascadeType.ALL)
    private List<SpotImage> spotImageList = new ArrayList<>();

    @OneToMany(mappedBy = "spot", cascade = CascadeType.ALL)
    private List<FacilityInformation> facilityInformationList = new ArrayList<>();
}
