package com.fisa.wonq.merchant.controller;

import com.fisa.wonq.global.response.ApiResponse;
import com.fisa.wonq.global.response.ResponseCode;
import com.fisa.wonq.global.security.resolver.Account;
import com.fisa.wonq.global.security.resolver.CurrentAccount;
import com.fisa.wonq.merchant.controller.dto.DiningTableRequest;
import com.fisa.wonq.merchant.controller.dto.DiningTableResponse;
import com.fisa.wonq.merchant.controller.dto.MerchantInfoResponse;
import com.fisa.wonq.merchant.service.DiningTableService;
import com.fisa.wonq.merchant.service.MerchantService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/merchant")
@RequiredArgsConstructor
public class MerchantController {

    private final MerchantService merchantService;
    private final DiningTableService diningTableService;


    @GetMapping
    @Operation(summary = "가맹점 기본 정보 조회",
            description = "현재 로그인된 회원의 가맹점 기본 정보를 반환합니다.")
    public ResponseEntity<ApiResponse<MerchantInfoResponse>> getMerchantInfo(
            @CurrentAccount Account account
    ) {
        MerchantInfoResponse dto = merchantService.getMerchantInfo(account.id());
        return ResponseEntity.ok(ApiResponse.of(ResponseCode.SUCCESS, dto));
    }

    @PostMapping("/tables")
    @Operation(summary = "테이블 추가",
            description = "매장 내 테이블을 추가합니다.")
    public ResponseEntity<ApiResponse<DiningTableResponse>> addTable(
            @CurrentAccount Account account,
            @RequestBody DiningTableRequest request
    ) {
        DiningTableResponse resp = diningTableService.addDiningTable(account, request);
        return ResponseEntity.ok(ApiResponse.of(ResponseCode.SUCCESS, resp));
    }
}
