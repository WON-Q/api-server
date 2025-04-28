package com.fisa.wonq.merchant.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Schema(description = "가맹점 이미지 업로드 응답")
public class MerchantImageResponse {
    @Schema(description = "업로드된 이미지 URL")
    private String imageUrl;
}
