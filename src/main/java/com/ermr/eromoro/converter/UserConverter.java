package com.ermr.eromoro.converter;

import com.ermr.eromoro.domain.User;
import com.ermr.eromoro.dto.UserRequestDTO;
import com.ermr.eromoro.dto.UserResponseDTO;

import java.time.LocalDateTime;

public class UserConverter {

    public static User toUser(UserRequestDTO.SignUpDto request) {

        return User.builder()
                .nickname(request.getNickname())
                .username(request.getUsername())
                .password(request.getPassword())
                .userType(request.getUserType())
                .build();
    }

    public static UserResponseDTO.SignUpResultDTO toSignUpResultDTO (User user) {
        return UserResponseDTO.SignUpResultDTO.builder()
                .nickname(user.getNickname())
                .build();
    }

    public static UserResponseDTO.SignUpExceptionDTO toSignUpExceptionDTO (Integer flag) {
        return UserResponseDTO.SignUpExceptionDTO.builder()
                .flag(flag)
                .build();
    }
}
