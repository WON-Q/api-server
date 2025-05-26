package com.fisa.wonq.order.service;

import com.fisa.wonq.order.controller.dto.req.OrderPaymentVerifyRequestDto;
import com.fisa.wonq.order.controller.dto.res.OrderVerifyResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderPaymentVerifyService {

    private final OrderService orderService;

    /**
     * 원큐오더 클라이언트로부터 받은 결제 검증 요청을 처리
     *
     * @param req 클라이언트 검증 요청
     * @param token 인증 토큰
     * @return 검증 결과
     */
    @Transactional
    public OrderVerifyResponse verify(OrderPaymentVerifyRequestDto req, String token) {

        log.info("[Service] 결제 검증 요청 처리 시작: orderCode={}, paymentId={}",
                req.getOrderCode(), req.getPaymentId());

        try {
            // OrderService의 verifyOrder 메서드 호출 전
            log.info("[Service] OrderService.verifyOrder 호출 직전");

            OrderVerifyResponse response = orderService.verifyOrder(
                    req.getOrderCode(),
                    req.getPaymentId()
            );

            // 응답 수신 후
            log.info("[Service] OrderService.verifyOrder 응답 수신: orderStatus={}, paymentStatus={}",
                    response.getOrderStatus(), response.getPaymentStatus());

            return response;
        } catch (Exception e) {
            log.error("[Service] 결제 검증 처리 중 오류 발생: {}", e.getMessage(), e);
            throw e;
        }
    }


}