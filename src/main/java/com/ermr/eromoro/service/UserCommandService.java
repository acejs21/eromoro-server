package com.ermr.eromoro.service;

import com.ermr.eromoro.domain.User;
import com.ermr.eromoro.dto.UserRequestDTO;
import com.ermr.eromoro.dto.UserResponseDTO;

public interface UserCommandService {

    User signUp(UserRequestDTO.SignUpDto request);
    UserResponseDTO.SignInResultDTO signIn(UserRequestDTO.SignInDto request);
    void logout(String accessToken);
    UserResponseDTO.RefreshAccessTokenResultDTO refreshAccessToken(String refreshToken);
    String generateAccessToken(Long userId, int accessExpTime);
    String generateAndSaveRefreshToken(String key, int refreshExpTime);
}
