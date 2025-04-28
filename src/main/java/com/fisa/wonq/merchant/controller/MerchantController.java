package com.fisa.wonq.merchant.controller;

import com.fisa.wonq.global.response.ApiResponse;
import com.fisa.wonq.global.response.ResponseCode;
import com.fisa.wonq.global.security.resolver.Account;
import com.fisa.wonq.global.security.resolver.CurrentAccount;
import com.fisa.wonq.merchant.controller.dto.req.DiningTableRequest;
import com.fisa.wonq.merchant.controller.dto.res.DiningTableDetailResponse;
import com.fisa.wonq.merchant.controller.dto.res.DiningTableResponse;
import com.fisa.wonq.merchant.controller.dto.res.MerchantImageResponse;
import com.fisa.wonq.merchant.controller.dto.res.MerchantInfoResponse;
import com.fisa.wonq.merchant.service.MerchantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/merchant")
@RequiredArgsConstructor
public class MerchantController {

    private final MerchantService merchantService;


    @GetMapping("/info")
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
        DiningTableResponse resp = merchantService.addDiningTable(account, request);
        return ResponseEntity.ok(ApiResponse.of(ResponseCode.SUCCESS, resp));
    }

    @GetMapping("/tables")
    @Operation(summary = "매장 내 모든 테이블 + 주문 내역 조회",
            description = "현재 로그인된 회원의 매장에 속한 모든 테이블과, 각 테이블의 주문 내역을 반환합니다.")
    public ResponseEntity<ApiResponse<List<DiningTableDetailResponse>>> listTablesWithOrders(
            @CurrentAccount Account account
    ) {
        List<DiningTableDetailResponse> dtos = merchantService.getDiningTablesWithOrders(account.id());
        return ResponseEntity.ok(ApiResponse.of(ResponseCode.SUCCESS, dtos));
    }

    @PostMapping(
            path = "/image",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "가맹점 대표 이미지 업로드",
            description = "로그인된 회원의 가맹점 대표 이미지를 S3에 업로드하고 URL을 반환합니다.")
    public ResponseEntity<ApiResponse<MerchantImageResponse>> uploadImage(
            @Parameter(description = "대표 이미지 파일", required = true)
            @RequestPart("file") MultipartFile file
    ) throws IOException {
        MerchantImageResponse resp = merchantService.uploadMerchantImage(file);
        return ResponseEntity.ok(ApiResponse.of(ResponseCode.SUCCESS, resp));
    }
}
