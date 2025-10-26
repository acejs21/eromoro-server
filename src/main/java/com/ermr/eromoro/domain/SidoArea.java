package com.ermr.eromoro.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sido_areas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SidoArea {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String name;

    private int code;

    @OneToMany(mappedBy = "sidoArea", cascade = CascadeType.ALL)
    private List<SiggArea> siggAreaList = new ArrayList<>();
}
