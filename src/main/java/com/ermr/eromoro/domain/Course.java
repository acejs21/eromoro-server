package com.ermr.eromoro.domain;

import com.ermr.eromoro.domain.common.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "courses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Course extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, columnDefinition = "int default 0")
    private int likeCount;

    @Column(nullable = false)
    private int duration;

    @Column(nullable = false)
    private float distance;

    @Column(nullable = false, columnDefinition = "int default 0")
    private int rating;

    private boolean isPublic;

    private int rampCount;

    private int stepCount;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("seq ASC")
    @Builder.Default
    @com.fasterxml.jackson.annotation.JsonManagedReference // ???
    private List<CoursePoint> coursePointList = new ArrayList<>();

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL)
    private List<LikeCourse> likeCourseList = new ArrayList<>();

    /* 편의 메서드 */
    public void setUser(User newUser) {
        // 0) 동일 대상이면 아무 것도 안 함
        if (this.user == newUser) return;
        // 1) 이전 관계 끊기: 기존 user 컬렉션에서 FeedbackList 제거
        if (this.user != null) {
            this.user.getCourseList().remove(this);
        }
        // 2) 필드 교체
        this.user = newUser;
        // 3) 새 관계에 추가 (null/중복 방지)
        if (newUser != null && !newUser.getCourseList().contains(this)) {
            newUser.getCourseList().add(this);
        }
    }

    public void updateCourseInfo(String title, boolean isPublic, int rating) {
        this.title = title;
        this.rating = rating;
        this.isPublic = isPublic;
    }

//    // 영범 코드
//    // Course <-> CoursePoint 양방향
//    // Course 쪽  OneToMany 설정
//    // 부모(Course)만 저장해도 자식(CoursePoint)가 저장
//    @OneToMany(mappedBy = "course",
//            cascade = CascadeType.ALL,
//            orphanRemoval = true)
//    @OrderBy("seq ASC")
//    @Builder.Default
//    @com.fasterxml.jackson.annotation.JsonManagedReference
//    private List<CoursePoint> points = new ArrayList<>();
//
//    public void addPoint(CoursePoint point) {
//        points.add(point);
//        point.setCourse(this);
//    }
}
