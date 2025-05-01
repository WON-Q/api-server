package com.fisa.wonq.order.service;

import com.fisa.wonq.order.domain.PaymentResult;
import com.fisa.wonq.order.domain.enums.PaymentMethod;

public interface PaymentService {
    /**
     * PG사에 결제 요청을 보내고 결과를 돌려받습니다.
     *
     * @param orderCode 내부 주문 유니크한 String 값
     * @param amount    결제할 금액
     * @param method    결제 수단 (CARD/CASH)
     * @return 결제 결과를 담은 PaymentResult
     */
    PaymentResult charge(String orderCode, Integer amount, PaymentMethod method);
}


