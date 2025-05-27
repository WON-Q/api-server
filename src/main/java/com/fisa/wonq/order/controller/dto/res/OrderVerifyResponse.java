package com.fisa.wonq.order.controller.dto.res;

import com.fisa.wonq.order.domain.enums.OrderStatus;
import com.fisa.wonq.order.domain.enums.PaymentStatus;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 원큐오더 서버가 원큐오더 클라이언트로 결제 검증 응답을 보내는 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderVerifyResponse {
    private String orderCode;
    private OrderStatus orderStatus;
    private PaymentStatus paymentStatus;
    private LocalDateTime verifiedAt;
    private String message;
}
