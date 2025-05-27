package com.fisa.wonq.order.feign.pg;

import com.fisa.wonq.global.config.feign.PgClientConfig;
import com.fisa.wonq.order.feign.pg.dto.BaseResponse;
import com.fisa.wonq.order.feign.pg.dto.PaymentDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "pgClient", url = "${app.pg.endpoint}", configuration = PgClientConfig.class)
public interface PgFeignClient {

    /**
     * orderCode에 해당하는 결제 정보를 조회하는 API
     *
     * @param orderCode 주문 코드
     * @return
     */
    @PostMapping("/api/payments/verify")
    ResponseEntity<BaseResponse<PaymentDto>> getPaymentByOrderCode(
            @PathVariable("orderCode") String orderCode
    );
}
