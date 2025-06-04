package com.fisa.wonq.global.websocket.service;

import com.fisa.wonq.global.websocket.dto.OrderNotificationMessage;
import com.fisa.wonq.order.domain.Order;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 가맹점 알림 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MerchantNotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    /**
     * 새 주문 알림을 가맹점에 전송하는 메서드
     *
     * @param order 주문 정보
     */
    public void sendNewOrderNotification(Order order) {
        try {
            Long merchantId = order.getDiningTable().getMerchant().getMerchantId();
            String destination = "/topic/merchant/" + merchantId + "/orders";
            
            log.debug("웹소켓 알림 준비 중: 목적지={}, 주문코드={}", destination, order.getOrderCode());
            
            OrderNotificationMessage message = OrderNotificationMessage.builder()
                    .orderCode(order.getOrderCode())
                    .merchantId(merchantId)
                    .tableNumber(order.getDiningTable().getTableNumber())
                    .orderStatus(order.getOrderStatus())
                    .paymentStatus(order.getPaymentStatus())
                    .totalAmount(order.getTotalAmount())
                    .timestamp(LocalDateTime.now())
                    .message("새로운 주문이 접수되었습니다.")
                    .build();
            
            // 특정 가맹점에게 알림 전송
            messagingTemplate.convertAndSend(destination, message);
            
            log.info("새 주문 알림 전송 완료: 목적지={}, 주문코드={}, 가맹점ID={}", destination, order.getOrderCode(), merchantId);

        } catch (Exception e) {
            log.error("주문 알림 전송 실패: {}", e.getMessage(), e);
            e.printStackTrace();  // 스택 트레이스 출력
        }
    }
}
