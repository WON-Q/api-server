package com.fisa.wonq.member.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

public class AuthRequestDTO {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "로그인 요청 DTO")
    public static class LoginRequest {
        @Schema(description = "아이디", example = "owner123")
        private String accountId;

        @Schema(description = "비밀번호", example = "P@ssw0rd!")
        private String password;
    }
}
