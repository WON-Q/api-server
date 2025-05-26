package com.fisa.wonq.order.controller.dto.req;

import lombok.*;

/**
 * 원큐오더 클라이언트로부터 결제 검증 요청을 받는 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderPaymentVerifyRequestDto {
    private String orderCode;
    private String paymentId;
}