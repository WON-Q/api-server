package com.fisa.wonq.order.domain;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PaymentResult {
    private final boolean success;
    private final String transactionId;
    private final LocalDateTime paidAt;

    public PaymentResult(boolean success, String transactionId, LocalDateTime paidAt) {
        this.success = success;
        this.transactionId = transactionId;
        this.paidAt = paidAt;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public LocalDateTime getPaidAt() {
        return paidAt;
    }
}
