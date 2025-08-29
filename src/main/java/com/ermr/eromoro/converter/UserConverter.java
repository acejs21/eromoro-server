package com.ermr.eromoro.converter;

import com.ermr.eromoro.domain.User;
import com.ermr.eromoro.dto.UserRequestDTO;
import com.ermr.eromoro.dto.UserResponseDTO;

import java.time.LocalDateTime;

public class UserConverter {

    public static UserResponseDTO.SignUpResultDTO toSignUpResultDTO (User user) {
        return UserResponseDTO.SignUpResultDTO.builder()
                .userId(user.getId())
                .createdAt(LocalDateTime.now())
                .build();
    }

    public static User toUser(UserRequestDTO.SignupDto request) {

        return User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(request.getPassword())
                .build();
    }

    public static UserResponseDTO.SignUpExceptionDTO toSignUpExceptionDTO (Integer flag) {
        return UserResponseDTO.SignUpExceptionDTO.builder()
                .flag(flag)
                .build();
    }
}
