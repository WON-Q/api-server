package com.fisa.wonq.member.controller.dto.res;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class AuthResponseDTO {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "로그인 응답 DTO")
    public static class LoginResponse {
        @Schema(description = "액세스 토큰")
        private String accessToken;

        @Builder.Default
        @Schema(description = "토큰 타입", example = "Bearer")
        private String tokenType = "Bearer";
    }
}
