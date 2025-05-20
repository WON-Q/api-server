package com.fisa.wonq.order.feign.pg;

import com.fisa.wonq.global.config.feign.PgClientConfig;
import com.fisa.wonq.order.feign.pg.dto.BaseResponse;
import com.fisa.wonq.order.feign.pg.dto.PaymentVerifyRequestDto;
import com.fisa.wonq.order.feign.pg.dto.PaymentVerifyResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;

@FeignClient(name = "pgClient", url = "${app.pg.endpoint}", configuration = PgClientConfig.class)
public interface PgFeignClient {

    /**
     * 결제 검증 API
     *
     * @param dto 결제 검증 요청 DTO
     * @return 결제 검증 응답
     */
    ResponseEntity<BaseResponse<PaymentVerifyResponseDto>> verifyPayment(
            PaymentVerifyRequestDto dto
    );

}
