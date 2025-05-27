package com.fisa.wonq.order.feign.pg.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * PG사로 환불 응답 DTO
 *
 * 원큐오더 클라이언트로부터 환불 요청을 받는 DTO와 유사하지만,
 * 결제 ID와 트랜잭션 ID, 결제 상태를 포함합니다.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RefundResponseDto {

    private Long paymentId;

    private String txnId;

    private PaymentStatus paymentStatus;
}