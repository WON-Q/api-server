package com.fisa.wonq.merchant.domain.enums;

public enum TableStatus {
    READY,          // 사용 가능한 상태(손님이 없는 상태) -> 손님이 나갈 때 점주 입장에서 테이블 상태 초기화할 때 필요한 상태
    IN_PROGRESS     // 사용 중인 상태(주문 접수 된 상태)
}
