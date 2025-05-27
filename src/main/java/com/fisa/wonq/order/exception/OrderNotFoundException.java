package com.fisa.wonq.order.exception;

public class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException(String orderCode) {
        super("주문을 찾을 수 없습니다: " + orderCode);
    }
}