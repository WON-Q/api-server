package com.fisa.wonq.merchant.controller.dto.res;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Schema(description = "가맹점 기본 정보 조회 응답 DTO")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MerchantInfoResponse {
    @Schema(description = "매장 이름", example = "원큐식당")
    private String merchantName;

    @Schema(description = "사업자 등록번호", example = "502-81-62379")
    private String businessRegistrationNo;

    @Schema(description = "대표자명", example = "홍길동")
    private String merchantOwnerName;

    @Schema(description = "전화번호", example = "010-9876-5432")
    private String merchantOwnerPhoneNo;

    @Schema(description = "매장 주소", example = "서울특별시 강남구 테헤란로 123")
    private String merchantAddress;

    @Schema(description = "매장 소개 문구", example = "신선한 재료로 만드는 …")
    private String description;

    @Schema(description = "계좌 은행명", example = "우리은행")
    private String merchantAccountBankName;

    @Schema(description = "계좌 번호", example = "123-456-78901234")
    private String merchantAccount;

    @Schema(description = "예금주명", example = "홍길동")
    private String merchantAccountHolderName;
}
