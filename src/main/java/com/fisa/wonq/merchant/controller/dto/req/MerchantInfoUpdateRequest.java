package com.fisa.wonq.merchant.controller.dto.req;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "가맹점 기본정보 수정 요청 DTO")
public class MerchantInfoUpdateRequest {

    @Schema(description = "매장 전화번호", example = "010-9876-5432")
    private String merchantOwnerPhoneNo;

    @Schema(description = "매장 소개글", example = "신선한 재료로 만드는 맛집입니다.")
    private String description;

    @Schema(description = "계좌 은행명", example = "우리은행")
    private String merchantAccountBankName;

    @Schema(description = "계좌번호", example = "123-456-78901234")
    private String merchantAccount;

    @Schema(description = "예금주명", example = "홍길동")
    private String merchantAccountHolderName;
}
