package com.fisa.wonq.order.controller.dto.res;

import com.fisa.wonq.order.domain.enums.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "결제 준비 응답 DTO")
public class OrderPrepareResponse {
    @Schema(description = "주문 PK ID", example = "123")
    private Long orderId;

    @Schema(description = "가맹점 ID", example = "10")
    private Long merchantId;

    @Schema(description = "테이블 ID", example = "3")
    private Long tableId;

    @Schema(description = "주문 생성 일시")
    private LocalDateTime createdAt;

    @Schema(description = "주문 상태", example = "ORDERED")
    private OrderStatus orderStatus;

    @Schema(description = "총 주문 금액", example = "45000")
    private Integer totalAmount;
}
