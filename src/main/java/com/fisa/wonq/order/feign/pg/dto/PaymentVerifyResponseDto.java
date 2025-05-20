package com.fisa.wonq.order.feign.pg.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentVerifyResponseDto {

    /**
     * 결제 검증을 위한 트랜잭션 ID
     */
    private String transactionId;

    /**
     * 결제 검증 상태
     */
    private PaymentStatus status;

}
