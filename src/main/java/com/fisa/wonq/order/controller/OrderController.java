package com.fisa.wonq.order.controller;

import com.fisa.wonq.global.response.ApiResponse;
import com.fisa.wonq.global.response.ResponseCode;
import com.fisa.wonq.global.security.resolver.Account;
import com.fisa.wonq.global.security.resolver.CurrentAccount;
import com.fisa.wonq.order.controller.dto.req.OrderRequest;
import com.fisa.wonq.order.controller.dto.res.OrderDetailResponse;
import com.fisa.wonq.order.controller.dto.res.OrderResponse;
import com.fisa.wonq.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

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

    /**
     * 일별 주문 내역 조회
     */
    @GetMapping("/daily")
    @Operation(summary = "일별 주문 내역 조회",
            description = "date(yyyy-MM-dd) 파라미터로 지정한 날짜에 발생한 주문 내역을 반환합니다.")
    public ResponseEntity<ApiResponse<List<OrderDetailResponse>>> listDailyOrders(
            @CurrentAccount Account account,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        List<OrderDetailResponse> dtos = orderService.getDailyOrders(account.id(), date);
        return ResponseEntity.ok(ApiResponse.of(ResponseCode.SUCCESS, dtos));
    }

    /**
     * 월별 주문 내역 조회
     */
    @GetMapping("/monthly")
    @Operation(summary = "월별 주문 내역 조회",
            description = "year, month 파라미터로 지정한 연·월에 발생한 주문 내역을 반환합니다.")
    public ResponseEntity<ApiResponse<List<OrderDetailResponse>>> listMonthlyOrders(
            @CurrentAccount Account account,
            @RequestParam int year,
            @RequestParam int month
    ) {
        List<OrderDetailResponse> dtos = orderService.getMonthlyOrders(account.id(), year, month);
        return ResponseEntity.ok(ApiResponse.of(ResponseCode.SUCCESS, dtos));
    }
}
