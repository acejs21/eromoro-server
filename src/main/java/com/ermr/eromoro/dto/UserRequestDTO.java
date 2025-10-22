package com.ermr.eromoro.dto;

import com.ermr.eromoro.domain.enums.UserType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

public class UserRequestDTO {

    @Getter
    public static class SignUpDto {
        @NotBlank(message = "닉네임은 빈값일 수 없습니다.")
        String nickname;
        @NotBlank(message = "아이디는 빈값일 수 없습니다.")
        String username;
        @NotBlank(message = "비밀번호는 빈값일 수 없습니다.")
        String password;
        UserType userType;
    }

    @Getter
    public static class SignInDto {
        @NotBlank(message = "아이디는 빈값일 수 없습니다.")
        String username;
        @NotBlank(message = "비밀번호는 빈값일 수 없습니다.")
        String password;
    }

    @Getter
    public static class IsDto {
        @NotBlank(message = "아이디는 빈값일 수 없습니다.")
        String username;
        @NotBlank(message = "비밀번호는 빈값일 수 없습니다.")
        String password;
    }

    @Getter
    public static class CheckUsernameIsSameDTO {
        String username;
    }
}
