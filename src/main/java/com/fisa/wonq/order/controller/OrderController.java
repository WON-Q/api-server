package com.fisa.wonq.order.controller;

import com.fisa.wonq.global.response.ApiResponse;
import com.fisa.wonq.global.response.ResponseCode;
import com.fisa.wonq.order.controller.dto.req.OrderRequest;
import com.fisa.wonq.order.controller.dto.res.OrderResponse;
import com.fisa.wonq.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @Operation(summary = "주문 생성(결제 요청)",
            description = "비회원 주문: 테이블 ID, 메뉴/옵션/수량, 전체 결제 금액, 결제 수단을 받아 주문을 생성합니다.")
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder(
            @RequestBody OrderRequest request
    ) {
        OrderResponse resp = orderService.createOrder(request);
        return ResponseEntity.ok(ApiResponse.of(ResponseCode.SUCCESS, resp));
    }
}
