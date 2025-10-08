package com.ermr.eromoro.jwt;

import com.ermr.eromoro.apiResponse.status.ErrorStatus;
import com.google.gson.Gson;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.PatternMatchUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

@RequiredArgsConstructor
@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String[] whitelist = {
            "/users/signup",
            "/users/signin",
            "/swagger-ui/**",
            "/v3/**",
            "/favicon.ico"
    };
    private final JwtTokenProvider jwtTokenProvider;

    private static void checkAuthorizationHeader(String header) {
        if(header == null) {
            throw new CustomJwtException(ErrorStatus.JWT_TOKEN_NOT_RECEIVED);
        } else if (!header.startsWith("Bearer ")) {
            throw new CustomJwtException(ErrorStatus.JWT_TOKEN_OUT_OF_FORM);
        }
    }

    // 필터를 거치지 않을 URL(로그인, 회원가입) 을 설정하고, true 를 return 하면 현재 필터를 건너뛰고 다음 필터로 이동
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String requestURI = request.getRequestURI();

        return PatternMatchUtils.simpleMatch(whitelist, requestURI);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        String requestUri = request.getRequestURI();
        String refreshToken = request.getHeader("refreshToken");

        // 특정 경로(토큰 갱신 api)에서만 Refresh Token 처리
        if ("/users/refresh/access-token".equals(requestUri)) {
            if (refreshToken != null) {
                try {
                    // access token 검증
                    checkAuthorizationHeader(authHeader);   // header 가 올바른 형식인지 체크
                    String token = jwtTokenProvider.getTokenFromHeader(authHeader);
                    Claims claims = jwtTokenProvider.validateTokenOnlySignature(token); // 토큰 검증
                    Authentication authentication = jwtTokenProvider.getAuthenticationFromExpiredAccessToken(token); // 사용자 인증 정보 생성
                    SecurityContextHolder.getContext().setAuthentication(authentication); // 사용자 인증 정보 저장
                    // refresh token 검증
                    jwtTokenProvider.validateRefreshToken(refreshToken); // 토큰 검증
                    filterChain.doFilter(request, response);    // 다음 필터로 이동
                } catch (Exception e) {
                    Gson gson = new Gson();
                    String json = "";
                    if (e instanceof CustomJwtException) {
//                        if(((CustomJwtException) e).getCode().equals("AUTHORIZATION4002")) { // Accesstoken 갱신시 Accesstoken 만료됐을 때 이렇게 처리하는게 좋아보임. (2025/10/06)
//                            System.out.println("Access Token Expired");
//                            return; // Access Token이 만료된 상황
//                        }
                        request.setAttribute("exceptionCode", ((CustomJwtException) e).getCode());
                        request.setAttribute("exceptionMessage", e.getMessage());
                        request.setAttribute("exceptionHttpStatus", ((CustomJwtException) e).getHttpStatus());
                        throw new InsufficientAuthenticationException(e.getMessage()); // EntryPoint로 넘김
                    }
                    else {
                        json = gson.toJson(Map.of("error", e.getMessage()));
                    }

                    response.setContentType("application/json; charset=UTF-8");
                    PrintWriter printWriter = response.getWriter();
                    printWriter.println(json);
                    printWriter.close();
                }
            }
            return;
        }

        try {
            checkAuthorizationHeader(authHeader);   // header 가 올바른 형식인지 체크
            String accessToken = jwtTokenProvider.getTokenFromHeader(authHeader);
            jwtTokenProvider.validateToken(accessToken); // 토큰 검증
            jwtTokenProvider.isTokenBlacklisted(authHeader); // 블랙리스트 확인 = 로그아웃된 회원인지 확인
        } catch (Exception e) {
            Gson gson = new Gson();
            String json = "";
            if (e instanceof CustomJwtException) {
                request.setAttribute("exceptionCode", ((CustomJwtException) e).getCode());
                request.setAttribute("exceptionMessage", e.getMessage());
                request.setAttribute("exceptionHttpStatus", ((CustomJwtException) e).getHttpStatus());
                throw new InsufficientAuthenticationException(e.getMessage()); // EntryPoint로 넘김
            } else {
                json = gson.toJson(Map.of("error", e.getMessage()));
            }

            response.setContentType("application/json; charset=UTF-8");
            PrintWriter printWriter = response.getWriter();
            printWriter.println(json);
            printWriter.close();

            return;
        }
        // accessToken != null 처리해주어야함.

//        log.info("--------------------------- JwtVerifyFilter ---------------------------");

        try {
            checkAuthorizationHeader(authHeader);   // header 가 올바른 형식인지 체크
            String token = jwtTokenProvider.getTokenFromHeader(authHeader);
            jwtTokenProvider.validateToken(token); // 토큰 검증
//            jwtTokenProvider.isExpired(token); // 토큰 만료 검증 <- 토큰 검증에서 수행

            Authentication authentication = jwtTokenProvider.getAuthentication(token); // 사용자 인증 정보 생성
            log.info("authentication = {}", authentication);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            filterChain.doFilter(request, response);    // 다음 필터로 이동
        } catch (Exception e) {
            Gson gson = new Gson();
            String json = "";
            if (e instanceof CustomJwtException) {
                request.setAttribute("exceptionCode", ((CustomJwtException) e).getCode());
                request.setAttribute("exceptionMessage", e.getMessage());
                request.setAttribute("exceptionHttpStatus", ((CustomJwtException) e).getHttpStatus());
                throw new InsufficientAuthenticationException(e.getMessage()); // EntryPoint로 넘김
            } else {
                json = gson.toJson(Map.of("error", e.getMessage()));
            }

            response.setContentType("application/json; charset=UTF-8");
            PrintWriter printWriter = response.getWriter();
            printWriter.println(json);
            printWriter.close();
        }
    }
}
