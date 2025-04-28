package com.fisa.wonq.member.controller;

import com.fisa.wonq.global.response.ApiResponse;
import com.fisa.wonq.global.response.ResponseCode;
import com.fisa.wonq.global.security.jwt.JwtTokenProvider;
import com.fisa.wonq.member.controller.dto.AuthRequestDTO.LoginRequest;
import com.fisa.wonq.member.controller.dto.AuthResponseDTO.LoginResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    @Operation(summary = "로그인",
            description = "아이디/비밀번호로 인증 후 액세스 토큰을 발급합니다.")
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @RequestBody LoginRequest req
    ) {
        // 1) 인증 시도
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getAccountId(), req.getPassword())
        );

        // 2) Principal(UserDetailsImpl)에서 토큰 생성
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String token = jwtTokenProvider.createAccessToken((com.fisa.wonq.global.security.user.UserDetailsImpl) userDetails);

        // 3) 응답
        return ResponseEntity.ok(
                ApiResponse.of(ResponseCode.SUCCESS,
                        LoginResponse.builder()
                                .accessToken(token)
                                .build()
                )
        );
    }
}
