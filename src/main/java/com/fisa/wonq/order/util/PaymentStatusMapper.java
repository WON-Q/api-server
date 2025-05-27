package com.fisa.wonq.order.util;

import com.fisa.wonq.order.domain.enums.PaymentStatus;

public class PaymentStatusMapper {

    public static com.fisa.wonq.order.domain.enums.PaymentStatus toDomainStatus(com.fisa.wonq.order.feign.pg.dto.PaymentStatus pgStatus) {
        if (pgStatus == null) return null;

        return switch (pgStatus) {
            case SUCCEEDED -> PaymentStatus.COMPLETED;
            case FAILED -> PaymentStatus.FAILED;
            case CANCELLED -> PaymentStatus.CANCELED;
            case CREATED -> PaymentStatus.PENDING;
            default -> null; // EXPIRED, REFUND_FAILED 등은 무시
        };
    }
}
