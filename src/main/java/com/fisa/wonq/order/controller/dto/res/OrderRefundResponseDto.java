package com.fisa.wonq.order.controller.dto.res;

import com.fisa.wonq.order.domain.enums.OrderStatus;
import com.fisa.wonq.order.domain.enums.PaymentStatus;
import lombok.Builder;
import lombok.Getter;

/**
 * 원큐오더 서버가 원큐오더 클라이언트로 환불 응답을 보내는 DTO
 */
@Getter
@Builder
public class OrderRefundResponseDto {


    private String orderCode;

    private OrderStatus orderStatus;

    private PaymentStatus paymentStatus;

    private String message;
}