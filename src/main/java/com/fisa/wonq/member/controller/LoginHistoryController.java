package com.fisa.wonq.member.controller;

import com.fisa.wonq.global.response.ApiResponse;
import com.fisa.wonq.global.response.ResponseCode;
import com.fisa.wonq.global.security.resolver.Account;
import com.fisa.wonq.global.security.resolver.CurrentAccount;
import com.fisa.wonq.member.controller.dto.res.LoginHistoryResponse;
import com.fisa.wonq.member.service.LoginHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth/login/history")
@RequiredArgsConstructor
public class LoginHistoryController {

    private final LoginHistoryService loginHistoryService;

    @GetMapping
    @Operation(summary = "로그인 이력 조회",
            description = "현재 로그인된 회원의 로그인 이력을 페이징하여 반환합니다.")
    public ResponseEntity<ApiResponse<Page<LoginHistoryResponse>>> listLoginHistory(
            @CurrentAccount Account account,
            @PageableDefault(size = 20, sort = "loginAt", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        Page<LoginHistoryResponse> page = loginHistoryService
                .getLoginHistory(account.id(), pageable);
        return ResponseEntity.ok(ApiResponse.of(ResponseCode.SUCCESS, page));
    }
}
