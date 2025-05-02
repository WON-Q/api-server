package com.fisa.wonq.merchant.controller.dto.res;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "QR 코드 생성 응답 DTO")
public class QrCodeResponse {
    @Schema(description = "생성된 QR 코드 이미지 URL (S3)",
            example = "https://bucket.s3.ap-northeast-2.amazonaws.com/uuid.png")
    private String qrCodeImageUrl;
}
