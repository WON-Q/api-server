package com.fisa.wonq.member.controller.dto.res;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


public class MemberResponseDTO {
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(description = "회원가입 결과 DTO")
    public static class SignupResponse {
        @Schema(description = "회원 ID")
        private Long memberId;

        @Schema(description = "회원 아이디")
        private String accountId;

        @Schema(description = "가맹점 ID")
        private Long merchantId;
    }

}
