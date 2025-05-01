package com.fisa.wonq.order.controller.dto.req;

import com.fisa.wonq.order.domain.enums.PaymentMethod;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "주문 생성 요청 DTO")
public class OrderRequest {
    @Schema(description = "테이블 ID", example = "3", required = true)
    private Long tableId;

    @Schema(description = "전체 결제 금액", example = "35000", required = true)
    private Integer totalAmount;

    @Schema(description = "결제 수단", example = "CARD", required = true)
    private PaymentMethod paymentMethod;

    @Schema(description = "주문할 메뉴 목록", required = true)
    private List<OrderMenuRequest> menus;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "주문-메뉴 정보 (inner)")
    public static class OrderMenuRequest {
        @Schema(description = "메뉴 ID", example = "42", required = true)
        private Long menuId;

        @Schema(description = "수량", example = "2", required = true)
        private Integer quantity;

        @Schema(description = "선택된 옵션 ID 목록", example = "[5,6]")
        private List<Long> optionIds;
    }
}
