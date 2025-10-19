package com.ermr.eromoro.domain;

import com.ermr.eromoro.domain.enums.UserType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", length = 20)
    private String nickname;

    @Column(name = "email", length = 50)
    private String username;

    @Column(name = "password", length = 100)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_type")
    private UserType userType;

    @Column(name = "profile_image")
    private String profileImage;

    @Column(name = "google_provider_id", length = 50)
    private String googleProviderId;

    @Column(name = "kakao_provider_id", length = 50)
    private String kakaoProviderId;

    @Column(name = "feedback_reward")
    private int feedbackReward;

    @Column
    private boolean isCourse;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Column(name = "inactive_date", columnDefinition = "DATETIME(6)")
    private LocalDateTime inactiveDate;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Feedback> feedbackList = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Course> courseList = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<LikeCourse> likeCourseList = new ArrayList<>();

    public void updateIsCourse() {
        isCourse = true;
    }
}
