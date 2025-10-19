package com.ermr.eromoro.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;

@Entity
@Table(name = "course_points")
//// 영범 코드
//@Table(
//        name = "course_points",
//        indexes = {
//                @Index(name = "idx_course_points_course_id", columnList = "course_id")
//        }
//)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CoursePoint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "course_id")
    @OnDelete(action = OnDeleteAction.CASCADE) // ???
    @com.fasterxml.jackson.annotation.JsonBackReference // ???
    private Course course;

    private int seq;

    @Column(name = "latitude", precision = 10, scale = 6, nullable = false)
    private BigDecimal latitude;

    @Column(name = "longitude", precision = 10, scale = 6, nullable = false)
    private BigDecimal longitude;

    /* 편의 메서드 */
    public void setCourse(Course newCourse) {
        // 0) 동일 대상이면 아무 것도 안 함
        if (this.course == newCourse) return;
        // 1) 이전 관계 끊기: 기존 user 컬렉션에서 FeedbackList 제거
        if (this.course != null) {
            this.course.getCoursePointList().remove(this);
        }
        // 2) 필드 교체
        this.course = newCourse;
        // 3) 새 관계에 추가 (null/중복 방지)
        if (newCourse != null && !newCourse.getCoursePointList().contains(this)) {
            newCourse.getCoursePointList().add(this);
        }
    }

//    // 영범 코드
//    @ManyToOne(fetch = FetchType.LAZY, optional = false)
//    @JoinColumn(
//            name = "course_id",
//            nullable = false,
//            foreignKey = @ForeignKey(name = "fk_course_points_course")
//    )
//    @OnDelete(action = OnDeleteAction.CASCADE)
//    @com.fasterxml.jackson.annotation.JsonBackReference
//    private Course course;
}
