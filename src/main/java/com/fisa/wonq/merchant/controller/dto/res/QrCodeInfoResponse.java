package com.fisa.wonq.merchant.controller.dto.res;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "가맹점 QR 코드 정보 DTO")
public class QrCodeInfoResponse {
    @Schema(description = "QR 레코드 ID", example = "1")
    private Long id;

    @Schema(description = "QR에 담긴 URL", example = "https://your-frontend.com/merchantId/tableId/menus")
    private String targetUrl;

    @Schema(description = "S3에 업로드된 QR 이미지 URL",
            example = "https://bucket.s3.ap-northeast-2.amazonaws.com/uuid.png")
    private String imageUrl;

    @Schema(description = "생성 일시")
    private LocalDateTime createdAt;

    @Schema(description = "qr코드와 연결된 dining_tableid" ,example="10")
    private Long diningTableId;
}
