package com.fisa.wonq.global.websocket.dto;

import com.fisa.wonq.order.domain.enums.OrderStatus;
import com.fisa.wonq.order.domain.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 주문 알림 메시지
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderNotificationMessage {
    private String orderCode;           // 주문 코드
    private Long merchantId;            // 가맹점 ID
    private Integer tableNumber;        // 테이블 번호
    private Integer totalAmount;        // 총 주문 금액
    private OrderStatus orderStatus;    // 주문 상태
    private PaymentStatus paymentStatus; // 결제 상태
    private LocalDateTime timestamp;    // 알림 시간
    private String message;             // 알림 메시지
}
