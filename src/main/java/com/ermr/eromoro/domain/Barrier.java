package com.ermr.eromoro.domain;

import com.ermr.eromoro.domain.enums.BarrierType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "barriers")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Barrier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "course_id")
    private Course course;

    @Column(name = "latitude", precision = 10, scale = 6, nullable = false)
    private BigDecimal latitude;

    @Column(name = "longitude", precision = 10, scale = 6, nullable = false)
    private BigDecimal longitude;

    @Enumerated(EnumType.STRING)
    @Column(name = "barrier_type", nullable = false)
    private BarrierType barrierType;

    private String image;

    /* 편의 메서드 */
    public void setCourse(Course newCourse) {
        // 0) 동일 대상이면 아무 것도 안 함
        if (this.course == newCourse) return;
        // 1) 필드 교체
        this.course = newCourse;
    }
}
