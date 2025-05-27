package com.fisa.wonq.order.controller;

import com.fisa.wonq.order.controller.dto.req.OrderRefundRequestDto;
import com.fisa.wonq.order.controller.dto.res.OrderRefundResponseDto;
import com.fisa.wonq.order.service.OrderRefundService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderRefundController {

    private final OrderRefundService orderRefundService;

    @PostMapping("/refund")
    public ResponseEntity<OrderRefundResponseDto> refundOrder(
            @RequestBody OrderRefundRequestDto request) {

        log.info("[Controller] 주문 환불 요청: orderCode={}, paymentId={}",
                request.getOrderCode(), request.getPaymentId());

        OrderRefundResponseDto response = orderRefundService.refundOrder(request);

        log.info("[Controller] 주문 환불 응답: orderCode={}, orderStatus={}, message={}",
                response.getOrderCode(), response.getOrderStatus(), response.getMessage());

        return ResponseEntity.ok(response);
    }
}