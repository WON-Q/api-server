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
@Schema(description = "결제 준비 요청 DTO")
public class OrderPrepareRequest {
    @Schema(description = "테이블 ID", example = "1", required = true)
    private Long tableId;

    @Schema(description = "가맹점 ID", example = "1", required = true)
    private Long merchantId;

    @Schema(description = "주문할 메뉴 목록", required = true)
    private List<OrderMenu> menus;

    @Schema(description = "결제 수단(ENUM - CARD / CASH)", example = "CARD")
    private PaymentMethod paymentMethod;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "주문-메뉴 정보 (inner)")
    public static class OrderMenu {
        @Schema(description = "메뉴 ID", example = "42", required = true)
        private Long menuId;

        @Schema(description = "수량", example = "2", required = true)
        private Integer quantity;

        @Schema(description = "선택된 옵션 ID 목록", example = "[5,6]")
        private List<Long> optionIds;
    }
}
