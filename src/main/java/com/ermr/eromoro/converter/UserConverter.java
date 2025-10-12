package com.ermr.eromoro.converter;

import com.ermr.eromoro.domain.User;
import com.ermr.eromoro.dto.UserRequestDTO;
import com.ermr.eromoro.dto.UserResponseDTO;

import java.time.LocalDateTime;

public class UserConverter {

    public static User toUser(UserRequestDTO.SignUpDto request, String password) {
        return User.builder()
                .nickname(request.getNickname())
                .username(request.getUsername())
                .password(password)
                .userType(request.getUserType())
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
}
