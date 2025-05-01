package com.fisa.wonq.order.service;

import com.fisa.wonq.order.domain.PaymentResult;
import com.fisa.wonq.order.domain.enums.PaymentMethod;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class StubPaymentService implements PaymentService {
    @Override
    public PaymentResult charge(String orderCode, Integer amount, PaymentMethod method) {

        // 무조건 성공하도록 스텁
        String fakeTxnId = "STUB_TXN_" + UUID.randomUUID();
        return new PaymentResult(true, fakeTxnId, LocalDateTime.now());
    }
}
