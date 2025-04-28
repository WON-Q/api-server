package com.fisa.wonq.merchant.controller.dto;


import com.fisa.wonq.merchant.domain.enums.TableStatus;
import com.fisa.wonq.order.domain.enums.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "테이블 상세 조회 응답 DTO")
public class DiningTableDetailResponse {
    @Schema(description = "테이블 ID", example = "10")
    private Long diningTableId;

    @Schema(description = "테이블 번호", example = "5")
    private Integer tableNumber;

    @Schema(description = "좌석 수", example = "4")
    private Integer capacity;

    @Schema(description = "상태", example = "READY")
    private TableStatus status;

    @Schema(description = "위치 X", example = "10")
    private Integer locationX;

    @Schema(description = "위치 Y", example = "20")
    private Integer locationY;

    @Schema(description = "너비", example = "100")
    private Integer locationW;

    @Schema(description = "높이", example = "50")
    private Integer locationH;

    @Builder.Default
    @Schema(description = "이 테이블의 주문 내역")
    private List<OrderResponse> orders = Collections.emptyList();

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "주문 정보 DTO (inner)")
    public static class OrderResponse {
        @Schema(description = "주문 ID", example = "ORD-20250428-0001")
        private String orderId;

        @Schema(description = "총 결제 금액", example = "45000")
        private Integer totalAmount;

        @Schema(description = "주문 상태", example = "PAID")
        private OrderStatus orderStatus;

        @Schema(description = "주문 생성 일시")
        private LocalDateTime createdAt;

        @Builder.Default
        @Schema(description = "이 주문의 메뉴별 내역")
        private List<OrderMenuResponse> orderMenus = Collections.emptyList();
    }

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

        @Builder.Default
        @Schema(description = "선택된 옵션들")
        private List<OrderMenuOptionResponse> options = Collections.emptyList();
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "주문-메뉴 옵션 정보 DTO (inner)")
    public static class OrderMenuOptionResponse {
        @Schema(description = "옵션 ID", example = "200")
        private Long orderMenuOptionId;

        @Schema(description = "메뉴 옵션 ID", example = "300")
        private Long menuOptionId;

        @Schema(description = "옵션 이름", example = "샷 추가")
        private String optionName;

        @Schema(description = "옵션 가격", example = "500")
        private Integer optionPrice;
    }
}
