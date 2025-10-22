package com.ermr.eromoro.controller;

import com.ermr.eromoro.apiResponse.ApiResponse;
import com.ermr.eromoro.converter.UserConverter;
import com.ermr.eromoro.domain.User;
import com.ermr.eromoro.dto.UserRequestDTO;
import com.ermr.eromoro.dto.UserResponseDTO;
import com.ermr.eromoro.service.UserCommandService;
import com.ermr.eromoro.service.UserQueryService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserCommandService userCommandService;
    private final UserQueryService  userQueryService;

    @Operation(summary = "회원가입 [v1.1 (2025-10-13)]", description =
            "# [v1.1 (2025-10-13)](https://www.notion.so/API-24b5335b2072801abf1ee6036c06b00e?p=24b5335b207281cc8ef8f2065b480a2e&pm=s)" +
                    " 회원가입 API 입니다. 닉네임과 아이디, 그리고 패스워드를 body에 입력해주세요."
    )
    @PostMapping(
            value = "/signup",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ApiResponse<UserResponseDTO.SignUpResultDTO> signUp(
            @RequestPart(value = "request") @Valid UserRequestDTO.SignUpDto request,
            @RequestPart(value = "photo", required = false) MultipartFile image
    ) {
        User user = userCommandService.signUp(request);
        return ApiResponse.onSuccess(
                UserConverter.toSignUpResultDTO(user)
        );
    }

    @Operation(summary = "로그인 [v1.0 (2025-10-05)]", description =
            "# [v1.0 (2025-10-05)](https://www.notion.so/API-24b5335b2072801abf1ee6036c06b00e?p=24b5335b207281ddb5c3c3811c6c125d&pm=s)" +
                    " 로그인 API 입니다. 아이디와 패스워드를 body에 입력해주세요."
    )
    @PostMapping("/signin")
    public ApiResponse<UserResponseDTO.SignInResultDTO> signIn(
            @RequestBody @Valid UserRequestDTO.SignInDto request
    ) {
        return ApiResponse.onSuccess(
                userCommandService.signIn(request)
        );
    }

    @Operation(summary = "로그아웃 [v1.0 (2025-10-06)]", description =
            "# [v1.0 (2025-10-06)](https://www.notion.so/API-21e5da7802c581cca23dff937ac3f155?p=2265da7802c581da88d1faf700b112de&pm=s)" +
                    " 로그아웃 API 입니다. 아이디와 패스워드를 body에 입력해주세요."
    )
    @PostMapping("/logout")
    public ApiResponse<?> logout(
            @RequestHeader("Authorization") String accessToken
    ) {
        userCommandService.logout(accessToken);
        return ApiResponse.onSuccess("");
    }

    @Operation(summary = "Access Token 갱신 [v1.0 (2025-10-06)]", description =
            "# [v1.0 (2025-10-06)](https://www.notion.so/API-24b5335b2072801abf1ee6036c06b00e?p=24b5335b2072815c80ace432e5d14c2b&pm=s)" +
                    " Access Token 갱신 API 입니다. Access Token과 Refresh Token을 header에 입력해주세요."
    )
    @PostMapping("/refresh/access-token")
    public ApiResponse<UserResponseDTO.RefreshAccessTokenResultDTO> refreshAccessToken(
            @RequestHeader("RefreshToken") String refreshToken
    ) {
        return ApiResponse.onSuccess(
                userCommandService.refreshAccessToken(refreshToken)
        );
    }

    @Operation(summary = "아이디 중복 검사 [v1.0 (2025-10-22)]", description =
            "# [v1.0 (2025-10-22)](https://www.notion.so/API-24b5335b2072801abf1ee6036c06b00e?p=24b5335b2072815c8eb1c60afa39a2a0&pm=s)" +
                    " 아이디 중복 검사 API 입니다."
    )
    @PostMapping("/signup/same")
    public ApiResponse<UserResponseDTO.CheckUsernameIsSameResultDTO> checkUsernameIsSame(
            @RequestBody UserRequestDTO.CheckUsernameIsSameDTO request
    ) {
        return ApiResponse.onSuccess(
                UserConverter.toCheckUsernameIsSameResultDTO(userQueryService.checkUsernameIsSame(request))
        );
    }
}
