package com.ermr.eromoro.jwt;

import com.ermr.eromoro.apiResponse.BaseErrorCode;
import com.ermr.eromoro.apiResponse.ErrorReasonDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
        response.setContentType("application/json;charset=UTF-8");

        // 커스텀 코드와 메시지 꺼냄
        String errorCode = (String) request.getAttribute("exceptionCode");
        String errorMessage = (String) request.getAttribute("exceptionMessage");
        HttpStatus httpStatus = (HttpStatus) request.getAttribute("exceptionHttpStatus");

        if (errorCode == null) errorCode = "AUTH4001";
        if (errorMessage == null) errorMessage = "인증에 실패하였습니다.";

        ErrorReasonDTO errorReasonDTO = ErrorReasonDTO.builder()
                .message(errorMessage)
                .code(errorCode)
                .isSuccess(false)
                .httpStatus(httpStatus)
                .build();

        ObjectMapper mapper = new ObjectMapper();
        response.getWriter().write(mapper.writeValueAsString(errorReasonDTO));
    }
}
