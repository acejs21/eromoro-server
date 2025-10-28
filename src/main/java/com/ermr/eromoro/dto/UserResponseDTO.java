package com.ermr.eromoro.dto;

import com.ermr.eromoro.domain.enums.IsAvailable;
import com.ermr.eromoro.domain.enums.UserType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public class UserResponseDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SignUpResultDTO {
        String nickname;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SignInResultDTO {
        Long userId;
        String accessToken;
        String refreshToken;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SignUpExceptionDTO {
        Integer flag;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RefreshAccessTokenResultDTO {
        Long userId;
        String accessToken;
        String refreshToken;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CheckUsernameIsSameResultDTO {
        IsAvailable isAvailable;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetUserInfoResultDTO {
        String nickname;
        String username;
        UserType userType;
        String profileImage;
        int feedbackReward;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetMyPageInfoResultDTO {
        String username;
        String nickname;
        String profileImage;
        UserType userType;
        int feedbackReward;
        int courseCount;
        List<MyLikeCourseDTO> myLikeCourseDTOList;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MyLikeCourseDTO {
        String spotImage;
        String courseTitle;
    }
}
