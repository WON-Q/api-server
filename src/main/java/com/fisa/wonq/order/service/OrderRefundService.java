package com.fisa.wonq.order.service;

import com.fisa.wonq.order.controller.dto.req.OrderRefundRequestDto;
import com.fisa.wonq.order.controller.dto.res.OrderRefundResponseDto;
import com.fisa.wonq.order.domain.Order;
import com.fisa.wonq.order.domain.enums.OrderStatus;
import com.fisa.wonq.order.feign.pg.dto.BaseResponse;
import com.fisa.wonq.order.feign.pg.dto.PaymentStatus;
import com.fisa.wonq.order.repository.OrderRepository;
import com.fisa.wonq.order.feign.pg.PgFeignClient;
import com.fisa.wonq.order.feign.pg.dto.RefundRequestDto;
import com.fisa.wonq.order.feign.pg.dto.RefundResponseDto;
import com.fisa.wonq.order.util.PaymentStatusMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.fisa.wonq.order.util.PaymentStatusMapper.toDomainStatus;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderRefundService {

    private final OrderRepository orderRepository;
    private final PgFeignClient pgFeignClient;

    @Transactional
    public OrderRefundResponseDto refundOrder(OrderRefundRequestDto request) {
        log.info("[Service] 주문 환불 처리 시작: orderCode={}, paymentId={}",
                request.getOrderCode(), request.getPaymentId());

        // 주문 조회
        Order order = orderRepository.findByOrderCode(request.getOrderCode())
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다: " + request.getOrderCode()));

        // 주문 상태 확인
        if (order.getOrderStatus() != OrderStatus.PAID) {
            log.warn("[Service] 환불 불가: 결제 완료 상태가 아닌 주문입니다. orderStatus={}", order.getOrderStatus());
            return OrderRefundResponseDto.builder()
                    .orderCode(order.getOrderCode())
                    .orderStatus(order.getOrderStatus())
                    .message("결제 완료 상태의 주문만 환불할 수 있습니다.")
                    .build();
        }

        try {
            // PG 서버에 환불 요청
            RefundRequestDto pgRequest = new RefundRequestDto(request.getPaymentId());

            log.info("[Service] PG 환불 요청: authToken={}, paymentId={}", pgRequest.getPaymentId());

            ResponseEntity<BaseResponse<RefundResponseDto>> responseEntity =
                    pgFeignClient.refundPayment(pgRequest);
            log.info("[Service] PG 환불 응답: status={}, body={}",
                    responseEntity.getStatusCode(), responseEntity.getBody());

            if (responseEntity.getBody() == null || responseEntity.getBody().getData() == null) {
                throw new RuntimeException("PG 서버로부터 응답을 받지 못했습니다.");
            }

            RefundResponseDto pgDto = responseEntity.getBody().getData();

            RefundResponseDto pgResponse = new RefundResponseDto(
                    pgDto.getPaymentId(),
                    pgDto.getTxnId(),
                    pgDto.getPaymentStatus()
            );


            // 환불 결과에 따라 주문 상태 업데이트
            if (pgResponse.getPaymentStatus() == PaymentStatus.CANCELLED) {
                order.updateOrderStatus(OrderStatus.REFUNDED);
                orderRepository.save(order);

                log.info("[Service] 환불 성공: orderCode={}, newStatus={}",
                        order.getOrderCode(), order.getOrderStatus());

                return OrderRefundResponseDto.builder()
                        .orderCode(order.getOrderCode())
                        .orderStatus(order.getOrderStatus())
                        .paymentStatus(PaymentStatusMapper.toDomainStatus(pgResponse.getPaymentStatus()))
                        .message("환불이 완료되었습니다.")
                        .build();
            } else {
                log.warn("[Service] 환불 실패: PG 서버에서 환불 처리 실패. paymentStatus={}",
                        pgResponse.getPaymentStatus());

                return OrderRefundResponseDto.builder()
                        .orderCode(order.getOrderCode())
                        .orderStatus(order.getOrderStatus())
                        .paymentStatus(PaymentStatusMapper.toDomainStatus(pgResponse.getPaymentStatus()))
                        .message("환불 처리에 실패했습니다.")
                        .build();
            }
        } catch (Exception e) {
            log.error("[Service] 환불 처리 중 오류 발생: {}", e.getMessage(), e);

            return OrderRefundResponseDto.builder()
                    .orderCode(order.getOrderCode())
                    .orderStatus(order.getOrderStatus())
                    .message("환불 처리 중 오류가 발생했습니다: " + e.getMessage())
                    .build();
        }
    }
}