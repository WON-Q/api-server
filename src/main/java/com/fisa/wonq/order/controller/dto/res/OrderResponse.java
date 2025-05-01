package com.fisa.wonq.order.controller.dto.res;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "주문 생성 응답 DTO")
public class OrderResponse {
    @Schema(description = "생성된 주문 ID", example = "240405T1017_t3")
    private String orderCode;

    @Schema(description = "총 결제 금액", example = "35000")
    private Integer totalAmount;

    @Schema(description = "PG 거래 ID", example = "STUB_TXN_abc123456")
    private String paymentTransactionId;
}
