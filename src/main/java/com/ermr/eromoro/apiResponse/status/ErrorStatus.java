package com.ermr.eromoro.apiResponse.status;

import com.ermr.eromoro.apiResponse.BaseErrorCode;
import com.ermr.eromoro.apiResponse.ErrorReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseErrorCode {

    // 일반 에러
    _INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500", "서버 에러, 관리자에게 문의 바랍니다."),
    _BAD_REQUEST(HttpStatus.BAD_REQUEST,"COMMON400","잘못된 요청입니다."),
    _UNAUTHORIZED(HttpStatus.UNAUTHORIZED,"COMMON401","인증이 필요합니다."),
    _FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON403", "금지된 요청입니다."),

    // User 관련 에러
    USER_ALREADY_EXISTING_ACCOUNT(HttpStatus.BAD_REQUEST, "USER4001", "이미 존재하는 계정입니다."),
    USER_NOT_FOUND(HttpStatus.BAD_REQUEST, "USER4002", "해당 아이디를 가진 유저가 존재하지 않습니다."),
    USER_PASSWORD_NOT_MATCHED(HttpStatus.BAD_REQUEST, "USER4003", "비밀번호가 일치하지 않습니다."),

    // Feedback 관련 에러
    FEEDBACK_NOT_FOUND(HttpStatus.BAD_REQUEST, "FEEDBACK4001", "피드백을 찾을 수 없습니다."),
    FEEDBACK_ALREADY_LIKED(HttpStatus.BAD_REQUEST, "FEEDBACK4002", "이미 좋아요를 누른 피드백입니다."),
    FEEDBACK_NOT_LIKED(HttpStatus.BAD_REQUEST, "FEEDBACK4003", "좋아요를 누르지 않은 피드백입니다."),
    FEEDBACK_NOT_AUTHORIZED(HttpStatus.FORBIDDEN, "FEEDBACK4004", "피드백에 대한 권한이 없습니다."),

    // 인가 관련 에러
    AUTHORIZATION_EXCEPTION(HttpStatus.UNAUTHORIZED, "AUTHORIZATION4001", "인증에 실패하였습니다."),
    JWT_ACCESS_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "AUTHORIZATION4002", "AccessToken이 만료되었습니다."),
    JWT_REFRESH_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "AUTHORIZATION4003", "RefreshToken이 만료되었습니다."),
    LOGOUT_USER(HttpStatus.UNAUTHORIZED, "AUTHORIZATION4004", "로그아웃된 유저입니다."),
    JWT_TOKEN_NOT_RECEIVED(HttpStatus.UNAUTHORIZED, "AUTHORIZATION4005", "JWT 토큰이 전달되지 않았습니다."),
    JWT_TOKEN_OUT_OF_FORM(HttpStatus.UNAUTHORIZED, "AUTHORIZATION4006", "JWT 토큰의 형식이 올바르지 않습니다."),
    REFRESH_TOKEN_NOT_EQUAL(HttpStatus.BAD_REQUEST, "AUTHORIZATION4007", "RefreshToken이 일치하지 않습니다."),

    // 코스 관련 에러
    COURSE_NOT_FOUND(HttpStatus.BAD_REQUEST, "COURSE4001", "코스를 찾을 수 없습니다."),
    COURSE_NOT_AUTHORIZED(HttpStatus.FORBIDDEN, "COURSE4002", "코스에 대한 권한이 없습니다."),
    INVALID_COURSE_TYPE(HttpStatus.BAD_REQUEST, "COURSE4003", "유효하지 않은 코스 타입입니다."),
    COURSE_ALREADY_LIKED(HttpStatus.BAD_REQUEST, "COURSE4004", "이미 좋아요를 누른 코스입니다."),
    COURSE_NOT_LIKED(HttpStatus.BAD_REQUEST, "COURSE4005", "좋아요를 누르지 않은 코스입니다."),
    INVALID_COURSE_SPOT_ID(HttpStatus.BAD_REQUEST, "COURSE4006", "요청에 관광지 ID를 포함되지 않았습니다."),

    // 관광지 관련 에러
    SPOT_NOT_FOUND(HttpStatus.BAD_REQUEST, "SPOT4001", "관광지를 찾을 수 없습니다."),

    // 이미지 관련 에러
    IMAGE_FAIL(HttpStatus.BAD_REQUEST, "IMAGE4001", "이미지 업로드 중 오류가 발생했습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ErrorReasonDTO getReason() {
        return ErrorReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .build();
    }

    @Override
    public ErrorReasonDTO getReasonHttpStatus() {
        return ErrorReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .httpStatus(httpStatus)
                .build()
                ;
    }
}
