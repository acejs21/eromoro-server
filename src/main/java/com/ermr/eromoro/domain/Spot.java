package com.ermr.eromoro.domain;

import com.ermr.eromoro.domain.enums.ContentType;
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

    @Enumerated(EnumType.STRING)
    private ContentType contentType;

    @Column(columnDefinition = "TEXT")
    private String imageUrl;

    private String contentId;

    @OneToMany(mappedBy = "spot", cascade = CascadeType.ALL)
    private List<Course> courseList = new ArrayList<>();

    @OneToMany(mappedBy = "spot", cascade = CascadeType.ALL)
    private List<SpotImage> spotImageList = new ArrayList<>();

    @OneToMany(mappedBy = "spot", cascade = CascadeType.ALL)
    private List<SpotFacilityInformation> spotFacilityInformationList = new ArrayList<>();

    public void updateInfo(String overview) {
        this.information = overview;
    }
}
