package com.fisa.wonq.merchant.controller.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "QR 코드 생성 요청 DTO")
public class QrCodeRequest {
    @NotBlank
    @Schema(description = "QR 코드에 담을 대상 URL",
            example = "https://your-frontend.com/merchantId/tableId/menus",
            required = true)
    private String targetUrl;

    @Schema(description = "테이블 번호",
            example = "1",
            required = true)
    private Integer tableNumber;
}
