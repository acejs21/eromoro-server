package com.ermr.eromoro.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

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
    private String name;

    @Column(name = "email", length = 50)
    private String email;

    @Column(name = "password", length = 100)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "disability_type")
    private DisabilityType disabilityType;

    @Column(name = "profile_image")
    private String profileImage;

    @Column(name = "google_provider_id", length = 50)
    private String googleProviderId;

    @Column(name = "kakao_provider_id", length = 50)
    private String kakaoProviderId;

    @Column(name = "feedback_reward")
    private int feedbackReward;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Column(name = "inactive_date", columnDefinition = "DATETIME(6)")
    private LocalDateTime inactiveDate;

    public enum DisabilityType{
        WHEELCHAIR,
        ELDERLY
    }
}
