package com.ermr.eromoro.dto;

import lombok.Getter;

public class UserRequestDTO {

    @Getter
    public static class SignupDto {
        String name;
        String email;
        String password;
    }
}
