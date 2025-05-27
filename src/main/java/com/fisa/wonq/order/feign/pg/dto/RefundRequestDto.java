package com.fisa.wonq.order.feign.pg.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * PG사로 환불 요청 DTO
 *
 * 원큐오더 클라이언트로부터 환불 요청을 받는 DTO와 유사하지만,
 * 결제 ID만 포함되어 있습니다.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RefundRequestDto {

    private String paymentId;
}