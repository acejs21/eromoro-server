package com.ermr.eromoro.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "like_courses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LikeCourse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "course_id")
    private Course course;

    /* 편의 메서드: User 교체 */
    public void setUser(User newUser) {
        // 0) 동일 대상이면 아무 것도 안 함
        if (this.user == newUser) return;
        // 1) 이전 관계 끊기
        if (this.user != null) {
            this.user.getLikeCourseList().remove(this);
        }
        // 2) 필드 교체
        this.user = newUser;
        // 3) 새 관계에 추가 (null/중복 방지)
        if (newUser != null && !newUser.getLikeCourseList().contains(this)) {
            newUser.getLikeCourseList().add(this);
        }
    }

    /* 편의 메서드: Course 교체 */
    public void setCourse(Course newCourse) {
        // 0) 동일 대상이면 아무 것도 안 함
        if (this.course == newCourse) return;
        // 1) 이전 관계 끊기
        if (this.course != null) {
            this.course.getLikeCourseList().remove(this);
        }
        // 2) 필드 교체
        this.course = newCourse;
        // 3) 새 관계에 추가 (null/중복 방지)
        if (newCourse != null && !newCourse.getLikeCourseList().contains(this)) {
            newCourse.getLikeCourseList().add(this);
        }
    }

    /* 두 연관관계를 한 번에 설정하는 편의 메서드*/
    public void connect(User user, Course course) {
        setUser(user);
        setCourse(course);
    }
}
