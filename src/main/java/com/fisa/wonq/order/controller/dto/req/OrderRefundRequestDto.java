package com.fisa.wonq.order.controller.dto.req;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 원큐오더 클라이언트로부터 환불 요청을 받는 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrderRefundRequestDto {

    /**
     * 환불 요청에 대한 주문 코드
     * 예: 240405T1017_t3
     */
    private String orderCode;

    /**
     * 환불 요청에 대한 결제 ID
     * 예: STUB_TXN_abc123456
     */
    private String paymentId;
}