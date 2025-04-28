package com.fisa.wonq.member.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

public class MemberRequestDTO {

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "점주 회원가입 요청")
    public static class SignupRequest {
        // --- Member ---
        @Schema(description = "로그인용 아이디", example = "owner123")
        private String accountId;

        @Schema(description = "비밀번호 (영문+특수문자, 6자 이상)", example = "P@ssw0rd!")
        private String password;

        @Schema(description = "이메일", example = "owner@example.com")
        private String email;

        @Schema(description = "휴대전화번호", example = "010-1234-5678")
        private String phoneNo;

        // --- Merchant ---
        @Schema(description = "사업자등록번호 (OCR)", example = "502-81-62379")
        private String businessRegistrationNo;

        @Schema(description = "가맹점 이름", example = "원큐식당")
        private String merchantName;

        @Schema(description = "대표자명", example = "홍길동")
        private String merchantOwnerName;

        @Schema(description = "대표자 전화번호", example = "010-9876-5432")
        private String merchantOwnerPhoneNo;

        @Schema(description = "가맹점 연락용 이메일", example = "contact@wonq.co.kr")
        private String merchantEmail;

        @Schema(description = "개업일 (yyyy-MM-dd)", example = "2023-01-15")
        private String businessLaunchingDate;

        @Schema(description = "가맹점 주소", example = "서울특별시 강남구 테헤란로 123")
        private String merchantAddress;

        @Schema(description = "대표계좌번호", example = "123-456-78901234")
        private String merchantAccount;

        @Schema(description = "오픈 시간 (HH:mm)", example = "09:00")
        private String openTime;

        @Schema(description = "마감 시간 (HH:mm)", example = "21:00")
        private String closeTime;
    }
}
