package com.fisa.wonq.order.controller;

import com.fisa.wonq.global.response.ApiResponse;
import com.fisa.wonq.global.response.ResponseCode;
import com.fisa.wonq.global.security.resolver.Account;
import com.fisa.wonq.global.security.resolver.CurrentAccount;
import com.fisa.wonq.order.controller.dto.req.ChangeOrderMenuStatusRequest;
import com.fisa.wonq.order.controller.dto.req.OrderPrepareRequest;
import com.fisa.wonq.order.controller.dto.req.OrderRequest;
import com.fisa.wonq.order.controller.dto.res.OrderDetailResponse;
import com.fisa.wonq.order.controller.dto.res.OrderPrepareResponse;
import com.fisa.wonq.order.controller.dto.res.OrderResponse;
import com.fisa.wonq.order.controller.dto.res.OrderVerifyResponse;
import com.fisa.wonq.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

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

    @PostMapping("/prepare")
    @Operation(summary = "결제 준비(주문 생성 - 주문 대기 상태)",
            description = "주문 정보를 ORDERED/PENDING 상태로 저장하고, 결제 요청에 필요한 데이터를 반환합니다.")
    public ResponseEntity<ApiResponse<OrderPrepareResponse>> prepareOrder(
            @RequestBody OrderPrepareRequest req
    ) {
        OrderPrepareResponse resp = orderService.prepareOrder(req);
        return ResponseEntity.ok(ApiResponse.of(resp));
    }

    @GetMapping("/daily")
    @Operation(summary = "일별 주문 내역 조회 (페이징 + 금액 필터)",
            description = "date(yyyy-MM-dd), optional minAmount, maxAmount, page, size, sort 파라미터로 페이징된 결과를 반환합니다." +
                    "\n\n [요청 URL 예시] /api/v1/merchant/orders/daily?date=2025-05-01&minAmount=10000&maxAmount=50000&page=0&size=10&sort=createdAt,desc")
    public ResponseEntity<ApiResponse<Page<OrderDetailResponse>>> listDailyOrders(
            @CurrentAccount Account account,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) Integer minAmount,
            @RequestParam(required = false) Integer maxAmount,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        Page<OrderDetailResponse> page = orderService
                .getDailyOrders(account.id(), date, minAmount, maxAmount, pageable);
        return ResponseEntity.ok(ApiResponse.of(ResponseCode.SUCCESS, page));
    }

    @GetMapping("/monthly")
    @Operation(summary = "월별 주문 내역 조회 (페이징 + 금액 필터)",
            description = "year, month, optional minAmount, maxAmount, page, size 파라미터로 페이징된 결과를 반환합니다." +
                    "\n\n [요청 URL 예시] /api/v1/merchant/orders/monthly?year=2025&month=5&minAmount=10000&maxAmount=50000&page=1&size=20&sort=totalAmount,asc")
    public ResponseEntity<ApiResponse<Page<OrderDetailResponse>>> listMonthlyOrders(
            @CurrentAccount Account account,
            @RequestParam int year,
            @RequestParam int month,
            @RequestParam(required = false) Integer minAmount,
            @RequestParam(required = false) Integer maxAmount,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        Page<OrderDetailResponse> page = orderService
                .getMonthlyOrders(account.id(), year, month, minAmount, maxAmount, pageable);
        return ResponseEntity.ok(ApiResponse.of(ResponseCode.SUCCESS, page));
    }

    @PutMapping("/{orderMenuId}/status")
    @Operation(
            summary = "주문된 메뉴 상태 변경",
            description = "점주 입장에서 주문된 개별 메뉴의 상태를 ORDERED → SERVED 등으로 변경합니다."
    )
    public ResponseEntity<ApiResponse<Void>> changeStatus(
            @CurrentAccount Account account,
            @PathVariable Long orderMenuId,
            @RequestBody ChangeOrderMenuStatusRequest req
    ) {
        orderService.changeOrderMenuStatus(account.id(), orderMenuId, req.getStatus());
        return ResponseEntity.ok(ApiResponse.of(ResponseCode.SUCCESS));
    }

    @GetMapping("/code/{orderCode}")
    @Operation(
            summary = "주문 코드로 주문 내역 조회",
            description = "orderCode를 받아 그 주문의 메뉴·옵션·결제 정보를 반환합니다."
    )
    public ResponseEntity<ApiResponse<OrderDetailResponse>> getByCode(
            @PathVariable String orderCode
    ) {
        OrderDetailResponse dto = orderService.getOrderByCode(orderCode);
        return ResponseEntity.ok(ApiResponse.of(dto));
    }

    @PostMapping("/code/{orderCode}/verify")
    @Operation(
            summary = "주문 결제 검증",
            description = "PG사 결제 검증 API를 호출하여 결제 상태를 확인하고 주문 상태를 업데이트합니다."
    )
    public ResponseEntity<ApiResponse<OrderVerifyResponse>> verifyOrder(
            @PathVariable String orderCode,
            @RequestParam String transactionId
    ) {
        OrderVerifyResponse response = orderService.verifyOrder(orderCode, transactionId);
        return ResponseEntity.ok(ApiResponse.of(ResponseCode.SUCCESS, response));
    }
}

