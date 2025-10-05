package com.ermr.eromoro.controller;

import com.ermr.eromoro.apiResponse.ApiResponse;
import com.ermr.eromoro.converter.UserConverter;
import com.ermr.eromoro.domain.User;
import com.ermr.eromoro.dto.UserRequestDTO;
import com.ermr.eromoro.dto.UserResponseDTO;
import com.ermr.eromoro.service.UserCommandService;
import com.ermr.eromoro.service.UserCommandServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserCommandService userCommandService;

    @Operation(summary = "회원가입 [v1.0 (2025-10-05)]", description =
            "# [v1.0 (2025-10-05)](https://www.notion.so/API-24b5335b2072801abf1ee6036c06b00e?p=24b5335b207281cc8ef8f2065b480a2e&pm=s)" +
                    " 회원가입 API 입니다. 닉네임과 아이디, 그리고 패스워드를 body에 입력해주세요."
    )
    @PostMapping("/signup")
    public ApiResponse<UserResponseDTO.SignUpResultDTO> signUp(
            @RequestBody @Valid UserRequestDTO.SignUpDto request
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
}
