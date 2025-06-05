package com.fisa.wonq.global.websocket.service;

import com.fisa.wonq.global.websocket.dto.OrderNotificationMessage;
import com.fisa.wonq.order.domain.Order;
import com.fisa.wonq.order.domain.enums.OrderStatus;
import com.fisa.wonq.order.domain.enums.PaymentStatus;
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
     */
    public void sendNewOrderNotification(OrderNotificationMessage message) {
        try {
            String destination = "/topic/merchant/" + message.getMerchantId() + "/orders";
            
            log.debug("웹소켓 알림 준비 중: 목적지={}", destination);
            OrderNotificationMessage orderNotificationMessage = OrderNotificationMessage.builder()
                    .orderCode(message.getOrderCode())
                    .merchantId(message.getMerchantId())
                    .tableNumber(message.getTableNumber())
                    .totalAmount(message.getTotalAmount())
                    .orderStatus(message.getOrderStatus())
                    .paymentStatus(message.getPaymentStatus())
                    .timestamp(LocalDateTime.now())
                    .message(message.getMessage())
                    .build();
            
            // 특정 가맹점에게 알림 전송
            messagingTemplate.convertAndSend(destination, orderNotificationMessage);

        } catch (Exception e) {
            log.error("주문 알림 전송 실패: {}", e.getMessage(), e);
            e.printStackTrace();  // 스택 트레이스 출력
        }
    }
}
