package com.fisa.wonq.order.controller.dto.res;

import com.fisa.wonq.order.domain.enums.OrderMenuStatus;
import com.fisa.wonq.order.domain.enums.OrderStatus;
import com.fisa.wonq.order.domain.enums.PaymentMethod;
import com.fisa.wonq.order.domain.enums.PaymentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "주문 내역 조회 응답 DTO")
public class OrderDetailResponse {
    @Schema(description = "주문 코드", example = "240405T1017_t3")
    private String orderCode;

    @Schema(description = "테이블 번호", example = "3")
    private Integer tableNumber;

    @Schema(description = "총 결제 금액", example = "35000")
    private Integer totalAmount;

    @Schema(description = "주문 상태", example = "PAID")
    private OrderStatus orderStatus;

    @Schema(description = "결제 상태", example = "COMPLETED")
    private PaymentStatus paymentStatus;

    @Schema(description = "결제 수단", example = "CARD")
    private PaymentMethod paymentMethod;

    @Schema(description = "결제 완료 일시")
    private LocalDateTime paidAt;

    @Schema(description = "주문 생성 일시")
    private LocalDateTime createdAt;

    @Schema(description = "주문된 메뉴 목록")
    private List<OrderMenuResponse> menus;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "주문-메뉴 정보 DTO (inner)")
    public static class OrderMenuResponse {
        @Schema(description = "주문메뉴 ID", example = "100")
        private Long orderMenuId;

        @Schema(description = "메뉴 ID", example = "50")
        private Long menuId;

        @Schema(description = "메뉴 이름", example = "아메리카노")
        private String menuName;

        @Schema(description = "수량", example = "2")
        private Integer quantity;

        @Schema(description = "단가", example = "4500")
        private Integer unitPrice;

        @Schema(description = "총 가격", example = "9000")
        private Integer totalPrice;

        @Schema(description = "주문-메뉴 상태", example = "ORDERED")
        private OrderMenuStatus status;

        @Schema(description = "선택된 옵션 목록")
        private List<OrderMenuOptionResponse> options;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "주문-메뉴 옵션 정보 DTO (inner)")
    public static class OrderMenuOptionResponse {
        @Schema(description = "주문메뉴옵션 ID", example = "200")
        private Long orderMenuOptionId;

        @Schema(description = "메뉴옵션 ID", example = "300")
        private Long menuOptionId;

        @Schema(description = "옵션 이름", example = "샷 추가")
        private String optionName;

        @Schema(description = "옵션 가격", example = "500")
        private Integer optionPrice;
    }
}

