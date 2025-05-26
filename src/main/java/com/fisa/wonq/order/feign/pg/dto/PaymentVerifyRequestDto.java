package com.fisa.wonq.order.feign.pg.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * PG사로 결제 검증 요청 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentVerifyRequestDto {

    /**
     * 결제 검증을 위한 트랜잭션 ID
     */
    private String transactionId;
}
