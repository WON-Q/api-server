package com.fisa.wonq.merchant.controller.dto.res;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "가맹점 기본정보(대표 이미지 + 이름) 조회 응답 DTO")
public class MerchantOverviewResponse {
    @Schema(description = "가맹점 ID", example = "10")
    private Long merchantId;

    @Schema(description = "가맹점 이름", example = "홍콩반점")
    private String merchantName;

    @Schema(description = "대표 이미지 URL", example = "https://won-q-order-merchant.s3.ap-northeast-2.amazonaws.com/abc123.png")
    private String merchantImgUrl;
}
