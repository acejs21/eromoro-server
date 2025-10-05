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
    FEEDBACK_NOT_FOUND(HttpStatus.BAD_REQUEST, "FEEDBACK4001", "피드백을 찾을 수 없습니다.");

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
