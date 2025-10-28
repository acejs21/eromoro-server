package com.ermr.eromoro.converter;

import com.ermr.eromoro.domain.LikeCourse;
import com.ermr.eromoro.domain.User;
import com.ermr.eromoro.domain.enums.IsAvailable;
import com.ermr.eromoro.dto.UserRequestDTO;
import com.ermr.eromoro.dto.UserResponseDTO;

import java.util.List;

public class UserConverter {

    public static User toUser(UserRequestDTO.SignUpDto request, String password) {
        return User.builder()
                .nickname(request.getNickname())
                .username(request.getUsername())
                .password(password)
                .userType(request.getUserType())
                .isCourse(false)
                .build();
    }

    public static UserResponseDTO.SignUpResultDTO toSignUpResultDTO(User user) {
        return UserResponseDTO.SignUpResultDTO.builder()
                .nickname(user.getNickname())
                .build();
    }

    public static UserResponseDTO.SignInResultDTO toSignInResultDTO(User user, String accessToken, String refreshToken) {
        return UserResponseDTO.SignInResultDTO.builder()
                .userId(user.getId())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public static UserResponseDTO.RefreshAccessTokenResultDTO RefreshAccessTokenResultDTO(Long userId, String accessToken, String refreshToken) {
        return UserResponseDTO.RefreshAccessTokenResultDTO.builder()
                .userId(userId)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public static UserResponseDTO.SignUpExceptionDTO toSignUpExceptionDTO (Integer flag) {
        return UserResponseDTO.SignUpExceptionDTO.builder()
                .flag(flag)
                .build();
    }

    public static UserResponseDTO.CheckUsernameIsSameResultDTO toCheckUsernameIsSameResultDTO(IsAvailable isAvailable) {
        return UserResponseDTO.CheckUsernameIsSameResultDTO.builder()
                .isAvailable(isAvailable)
                .build();
    }

    public static UserResponseDTO.GetUserInfoResultDTO toUserInfo(User user) {
        return UserResponseDTO.GetUserInfoResultDTO.builder()
                .nickname(user.getNickname())
                .username(user.getUsername())
                .userType(user.getUserType())
                .profileImage(user.getProfileImage())
                .feedbackReward(user.getFeedbackReward())
                .build();
    }

    public static UserResponseDTO.GetMyPageInfoResultDTO toGetMyPageInfoResultDTO(User getUser, int courseCount, List<LikeCourse> getLikeCourseList) {
        List<UserResponseDTO.MyLikeCourseDTO> myLikeCourseDTOList = getLikeCourseList.stream()
                .map(UserConverter::toMyLikeCourseDTO)
                .toList();

        return UserResponseDTO.GetMyPageInfoResultDTO.builder()
                .nickname(getUser.getNickname())
                .username(getUser.getUsername())
                .userType(getUser.getUserType())
                .profileImage(getUser.getProfileImage())
                .feedbackReward(getUser.getFeedbackReward())
                .courseCount(courseCount)
                .myLikeCourseDTOList(myLikeCourseDTOList)
                .build();
    }

    public static UserResponseDTO.MyLikeCourseDTO toMyLikeCourseDTO(LikeCourse likeCourse) {
        return UserResponseDTO.MyLikeCourseDTO.builder()
                .spotImage(likeCourse.getCourse().getSpot() != null ? likeCourse.getCourse().getSpot().getImageUrl() : null)
                .courseTitle(likeCourse.getCourse().getTitle())
                .build();

    }
}
