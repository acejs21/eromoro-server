package com.ermr.eromoro.service;

import com.ermr.eromoro.apiResponse.exception.handler.UserHandler;
import com.ermr.eromoro.apiResponse.status.ErrorStatus;
import com.ermr.eromoro.converter.UserConverter;
import com.ermr.eromoro.domain.User;
import com.ermr.eromoro.dto.UserRequestDTO;
import com.ermr.eromoro.dto.UserResponseDTO;
import com.ermr.eromoro.jwt.JwtTokenProvider;
import com.ermr.eromoro.jwt.SecurityUtil;
import com.ermr.eromoro.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class UserCommandServiceImpl implements UserCommandService {

    @Value("${jwt.access-expiration}")
    private int accessExpTime;
    @Value("${jwt.refresh-expiration}")
    private int refreshExpTime;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate<String, String> redisTemplate;

    @Override
    @Transactional
    public User signUp(UserRequestDTO.SignUpDto request) {
        String password = passwordEncoder.encode(request.getPassword());
        User newUser = UserConverter.toUser(request, password);

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UserHandler(ErrorStatus.USER_ALREADY_EXISTING_ACCOUNT);
        }

        return userRepository.save(newUser);
    }

    @Override
    public UserResponseDTO.SignInResultDTO signIn(UserRequestDTO.SignInDto request) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword());
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        // jwt토큰(access token, refresh token) 생성
        User getUser = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new UserHandler(ErrorStatus.USER_NOT_FOUND));
        String key = "users:" + getUser.getId().toString();
        String accessToken = generateAccessToken(getUser.getId(), accessExpTime);
        String refreshToken = generateAndSaveRefreshToken(key, refreshExpTime);

        return UserConverter.toSignInResultDTO(getUser, accessToken, refreshToken);
    }

    @Override
    public void logout(String accessToken) {
        // 로그아웃시킬 회원의 refresh token redis에서 삭제
        Long userId = SecurityUtil.getCurrentUserId();
        String key = "users:" + userId.toString();
        redisTemplate.delete(key);

        // 로그아웃시킬 회원의 access token redis의 블랙리스트로 저장, 인가 처리시 블랙리스트 확인을 통해 로그아웃된 회원인지 확인함.
        key = "blackList:" + userId.toString();
        long tokenRemainTimeSecond = jwtTokenProvider.tokenRemainTimeSecond(accessToken);
        redisTemplate.opsForValue().set(key, accessToken, tokenRemainTimeSecond, TimeUnit.SECONDS);
    }

    @Override
    public UserResponseDTO.RefreshAccessTokenResultDTO refreshAccessToken(String refreshToken) {
        Long userId = SecurityUtil.getCurrentUserId();
        String key = "users:" + userId.toString();
        String accessToken;
        String newRefreshToken;
        // 전달된 refresh token과 redis의 refresh token비교
        String getRefreshTokenFromRedis = redisTemplate.opsForValue().get(key);

        if (refreshToken.equals(getRefreshTokenFromRedis)) {
            accessToken = generateAccessToken(userId, accessExpTime);
            newRefreshToken = generateAndSaveRefreshToken(key, refreshExpTime);
        } else {
            throw new UserHandler(ErrorStatus.REFRESH_TOKEN_NOT_EQUAL);
        }

        return UserConverter.RefreshAccessTokenResultDTO(userId, accessToken, newRefreshToken);
    }



    @Override
    public String generateAccessToken(Long userId, int accessExpTime) {
        // 인증 완료 후 jwt토큰(accessToken) 생성
        Map<String, Object> valueMap = Map.of(
                "userId", userId
        );
        return jwtTokenProvider.generateToken(valueMap, accessExpTime);
    }

    @Override
    public String generateAndSaveRefreshToken(String key, int refreshExpTime) {
        // 인증 완료 후 jwt토큰(refreshToken) 생성
        String refreshToken = jwtTokenProvider.generateToken(Collections.emptyMap(), refreshExpTime);
        redisTemplate.opsForValue().set(key, refreshToken, refreshExpTime, TimeUnit.SECONDS);
        return refreshToken;
    }

    @Override
    @Transactional
    public void updateUserInfo(Long userId, UserRequestDTO.UpdateUserInfoDTO request) {
        User getUser = userRepository.findById(userId).orElseThrow(() -> new UserHandler(ErrorStatus.USER_NOT_FOUND));

        if (request.getNickname() != null) {
            getUser.updateName(request.getNickname());
        }
        if (request.getUserType() != null) {
            getUser.updateUserType(request.getUserType());
        }
    }
}
