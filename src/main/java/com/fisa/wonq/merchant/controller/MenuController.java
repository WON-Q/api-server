package com.fisa.wonq.merchant.controller;

import com.fisa.wonq.global.response.ApiResponse;
import com.fisa.wonq.global.response.ResponseCode;
import com.fisa.wonq.global.security.resolver.Account;
import com.fisa.wonq.global.security.resolver.CurrentAccount;
import com.fisa.wonq.merchant.controller.dto.req.MenuRequest;
import com.fisa.wonq.merchant.controller.dto.res.MenuDetailResponse;
import com.fisa.wonq.merchant.controller.dto.res.MenuResponse;
import com.fisa.wonq.merchant.service.MenuService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/merchant/menus")
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;

    @PostMapping("/update")
    @Operation(summary = "메뉴 등록",
            description = "S3 URL로 업로드된 이미지 주소와 기본 정보, 옵션 그룹/옵션을 받아 메뉴를 등록합니다.")
    public ResponseEntity<ApiResponse<MenuResponse>> createMenu(
            @CurrentAccount Account account,
            @Valid @RequestBody MenuRequest request
    ) {
        MenuResponse resp = menuService.createMenu(account.id(), request);
        return ResponseEntity.ok(ApiResponse.of(ResponseCode.SUCCESS, resp));
    }

    @GetMapping("/{merchantId}/list")
    @Operation(
            summary = "매장 내 전체 메뉴 조회 (비회원용)",
            description = "merchantId 를 받아 해당 가맹점에 등록된 모든 메뉴와 옵션 그룹/옵션을 반환합니다."
    )
    public ResponseEntity<ApiResponse<List<MenuDetailResponse>>> listMenus(
            @PathVariable Long merchantId
    ) {
        List<MenuDetailResponse> dtos = menuService.getMenusWithOptionsByMerchantId(merchantId);
        return ResponseEntity.ok(ApiResponse.of(ResponseCode.SUCCESS, dtos));
    }
}
