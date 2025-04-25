package com.fisa.wonq.order.domain.enums;

public enum OrderStatus {
    ORDERED,    // 주문은 되었지만 미결제 상태
    PAID,       // 결제된 주문
    CANCELED,   // 주문취소
    REFUNDED,   // 환불된 주문

}
