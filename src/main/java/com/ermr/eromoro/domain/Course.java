package com.ermr.eromoro.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "courses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "spot_id")
    private Long spotId;

    @Column(name = "title")
    private String title;

    @Column(name = "step_count")
    private int stepCount;

    @Column(name = "ramp_count")
    private int rampCount;

    @Column(name = "like_count")
    private int likeCount;

    @Column(name = "duration")
    private int duration;

    @Column(name = "distance")
    private float distance;

    @Column(name = "is_public")
    private boolean isPublic;

    // Course <-> CoursePoint 양방향
    // Course 쪽  OneToMany 설정
    // 부모(Course)만 저장해도 자식(CoursePoint)가 저장
    @OneToMany(mappedBy = "course",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    @OrderBy("seq ASC")
    @Builder.Default
    @com.fasterxml.jackson.annotation.JsonManagedReference
    private List<CoursePoint> points = new ArrayList<>();

    public void addPoint(CoursePoint point) {
        points.add(point);
        point.setCourse(this);
    }
}
