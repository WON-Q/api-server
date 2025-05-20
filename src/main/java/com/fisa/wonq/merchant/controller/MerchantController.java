package com.fisa.wonq.merchant.controller;

import com.fisa.wonq.global.response.ApiResponse;
import com.fisa.wonq.global.response.ResponseCode;
import com.fisa.wonq.global.security.resolver.Account;
import com.fisa.wonq.global.security.resolver.CurrentAccount;
import com.fisa.wonq.merchant.controller.dto.req.*;
import com.fisa.wonq.merchant.controller.dto.res.*;
import com.fisa.wonq.merchant.service.MerchantService;
import com.fisa.wonq.merchant.service.QrCodeService;
import com.fisa.wonq.merchant.service.QrUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
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
    private final QrCodeService qrCodeService;
    private final QrUtils qrUtils;


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
    @Operation(summary = "대표 이미지 업로드(가맹점 대표 이미지, 메뉴 대표 이미지)",
            description = "로그인된 회원의 가맹점 대표 이미지를 S3에 업로드하고 URL을 반환합니다.")
    public ResponseEntity<ApiResponse<MerchantImageResponse>> uploadImage(
            @Parameter(description = "대표 이미지 파일", required = true)
            @RequestPart("file") MultipartFile file
    ) throws IOException {
        MerchantImageResponse resp = merchantService.uploadMerchantImage(file);
        return ResponseEntity.ok(ApiResponse.of(ResponseCode.SUCCESS, resp));
    }

    @PutMapping("/tables/{tableId}/status")
    @Operation(summary = "테이블 상태 초기화(READY 상태로)",
            description = "손님이 나간 후 IN_PROGRESS였던 테이블을 READY 상태로 변경합니다.")
    public ResponseEntity<ApiResponse<DiningTableStatusResponse>> resetTable(
            @CurrentAccount Account account,
            @PathVariable Long tableId,
            @RequestBody DiningTableStatusRequest req
    ) {
        DiningTableStatusResponse resp = merchantService.resetTableStatus(
                account.id(), tableId, req
        );
        return ResponseEntity.ok(ApiResponse.of(ResponseCode.SUCCESS, resp));
    }

    @PostMapping("/qr")
    @Operation(
            summary = "QR 코드 생성 및 S3 업로드",
            description = "메뉴 전체 목록 페이지 URL을 받아 QR 코드를 생성, S3에 업로드한 뒤 해당 이미지 URL을 반환합니다."
    )
    public ResponseEntity<ApiResponse<QrCodeResponse>> generateQr(
            @CurrentAccount Account account,
            @Valid @RequestBody QrCodeRequest request
    ) {
        String imageUrl = qrUtils.generateQrCodeAndUpload(account.id(), request.getTargetUrl());
        QrCodeResponse resp = QrCodeResponse.builder()
                .qrCodeImageUrl(imageUrl)
                .build();
        return ResponseEntity.ok(ApiResponse.of(resp));
    }

    @GetMapping("/qr")
    @Operation(summary = "가맹점 QR 목록 조회",
            description = "현재 로그인된 가맹점에 생성된 모든 QR 정보를 반환합니다.")
    public ResponseEntity<ApiResponse<List<QrCodeInfoResponse>>> listQrs(
            @CurrentAccount Account account
    ) {
        var qrCodes = qrCodeService.getQrCodes(account.id());
        var dtos = qrCodes.stream()
                .map(qr -> QrCodeInfoResponse.builder()
                        .id(qr.getId())
                        .targetUrl(qr.getTargetUrl())
                        .imageUrl(qr.getImageUrl())
                        .createdAt(qr.getCreatedAt())
                        .build())
                .toList();
        return ResponseEntity.ok(ApiResponse.of(dtos));
    }

    @PatchMapping("/info")
    @Operation(summary = "가맹점 기본정보 수정",
            description = "매장 전화번호·소개글·계좌정보를 일부만 수정할 수 있습니다.")
    public ResponseEntity<ApiResponse<MerchantInfoResponse>> updateMerchantInfo(
            @CurrentAccount Account account,
            @Valid @RequestBody MerchantInfoUpdateRequest req
    ) {
        MerchantInfoResponse dto = merchantService.updateMerchantInfo(account.id(), req);
        return ResponseEntity.ok(ApiResponse.of(ResponseCode.SUCCESS, dto));
    }

    @PutMapping("/tables/{tableId}")
    @Operation(summary = "테이블 정보 수정",
            description = "테이블 번호와 좌석 수를 부분적으로 수정합니다.")
    public ResponseEntity<ApiResponse<DiningTableUpdateResponse>> updateTableInfo(
            @CurrentAccount Account account,
            @PathVariable Long tableId,
            @Valid @RequestBody DiningTableUpdateRequest req
    ) {
        DiningTableUpdateResponse resp =
                merchantService.updateDiningTableInfo(account.id(), tableId, req);
        return ResponseEntity.ok(ApiResponse.of(ResponseCode.SUCCESS, resp));
    }

    @GetMapping("/{merchantId}/overview")
    @Operation(
            summary = "가맹점 대표 이미지·이름 조회(비회원용)",
            description = "merchantId로 해당 가맹점의 이름과 대표 이미지 URL을 반환합니다."
    )
    public ResponseEntity<ApiResponse<MerchantOverviewResponse>> getMerchantOverview(
            @PathVariable Long merchantId
    ) {
        MerchantOverviewResponse dto = merchantService.getMerchantOverview(merchantId);
        return ResponseEntity.ok(ApiResponse.of(dto));
    }
}
