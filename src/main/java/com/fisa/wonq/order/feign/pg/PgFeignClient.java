package com.fisa.wonq.order.feign.pg;

import com.fisa.wonq.global.config.feign.PgClientConfig;
import com.fisa.wonq.order.feign.pg.dto.BaseResponse;
import com.fisa.wonq.order.feign.pg.dto.PaymentDto;
import com.fisa.wonq.order.feign.pg.dto.RefundRequestDto;
import com.fisa.wonq.order.feign.pg.dto.RefundResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "pgClient", url = "${app.pg.endpoint}", configuration = PgClientConfig.class)
public interface PgFeignClient {

    /**
     * orderCode에 해당하는 결제 정보를 조회하는 API
     *
     * @param orderCode 주문 코드
     * @return
     */
    @GetMapping("/payments/orders/{orderCode}")
    ResponseEntity<BaseResponse<PaymentDto>> getPaymentByOrderCode(
            @PathVariable("orderCode") String orderCode
    );

    /**
     * 결제 환불 API
     *
     * @param dto 환불 요청 DTO
     * @return 환불 처리 응답
     */
    @PostMapping("/api/payments/refund")
    ResponseEntity<BaseResponse<RefundResponseDto>> refundPayment(
            @RequestBody RefundRequestDto dto
    );
}
