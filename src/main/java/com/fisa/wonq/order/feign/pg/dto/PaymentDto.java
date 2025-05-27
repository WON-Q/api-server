package com.fisa.wonq.order.feign.pg.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * PG사로부터 받은 결제 검증 응답 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDto {

    /**
     * PG사에서 발급한 결제 ID
     */
    private Long payementId;


    /**
     * 가맹점 내 주문 고유 ID
     */
    private String orderId;

    /**
     * 가맹점 Id
     */
    private Long merchantId;

    /**
     * 결제 상태
     */
    private PaymentStatus paymentStatus;

    /**
     * 결제 금액
     */
    private Long amount;

    /**
     * 결제 통화
     */
    private String currency;

    /**
     * 콜백 URL
     * 결제 완료 후 사용자를 리다이렉트할 URL
     */
    private String callbackUrl;

    /**
     * 결제 수단
     */
    private String paymentMethod;

    /**
     * 결제가 요청된 시간
     */
    private String requestAt;

    /**
     * 결제가 성공적으로 승인된 시간
     */
    private String approvedAt;

    /**
     * 결제가 취소된 시간
     */
    private String canceledAt;

    /**
     * 결제에 사용된 카드 번호
     */
    private String userCardNumber;
}
