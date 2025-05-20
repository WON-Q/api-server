package com.fisa.wonq.order.domain.enums;

public enum PaymentStatus {
    PENDING,    // 결제 대기
    COMPLETED,  // 결제 완료
    CANCELED,   // 결제 취소
    FAILED,     // 결제 실패
}
