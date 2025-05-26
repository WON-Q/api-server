package com.fisa.wonq.order.controller;

import com.fisa.wonq.order.controller.dto.req.OrderPaymentVerifyRequestDto;
import com.fisa.wonq.order.controller.dto.res.OrderVerifyResponse;
import com.fisa.wonq.order.service.OrderPaymentVerifyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderPaymentVerifyController {

    private final OrderPaymentVerifyService verifyService;
@PostMapping("/verify")
public ResponseEntity<OrderVerifyResponse> verifyPayment(
        @RequestBody OrderPaymentVerifyRequestDto requestDto,
        @RequestHeader(value = "Authorization", required = false) String token
) {
    // 토큰이 null이면 기본값 설정
    String authToken = (token != null) ? token : "Bearer test-token";

    log.info("[Controller] 결제 검증 요청 시작: orderCode={}, paymentId={}",
            requestDto.getOrderCode(), requestDto.getPaymentId());

    try {
        // 여기서 authToken을 사용해야 함 (token 대신)
        OrderVerifyResponse response = verifyService.verify(requestDto, authToken);
        log.info("[Controller] 결제 검증 완료: orderStatus={}, paymentStatus={}",
                response.getOrderStatus(), response.getPaymentStatus());
        return ResponseEntity.ok(response);
    } catch (Exception e) {
        log.error("[Controller] 결제 검증 처리 중 오류 발생: {}", e.getMessage(), e);
        throw e;
    }
}
}
