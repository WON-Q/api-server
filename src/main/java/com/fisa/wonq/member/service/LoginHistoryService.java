package com.fisa.wonq.member.service;

import com.fisa.wonq.global.security.exception.SecurityErrorCode;
import com.fisa.wonq.global.security.exception.SecurityException;
import com.fisa.wonq.member.controller.dto.res.LoginHistoryResponse;
import com.fisa.wonq.member.domain.LoginHistory;
import com.fisa.wonq.member.domain.Member;
import com.fisa.wonq.member.domain.enums.MemberStatus;
import com.fisa.wonq.member.repository.LoginHistoryRepository;
import com.fisa.wonq.member.repository.MemberRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class LoginHistoryService {
    private final LoginHistoryRepository loginHistoryRepository;
    private final MemberRepository memberRepository;

    /**
     * 로그인 성공 시 기록을 남깁니다.
     */
    @Transactional
    public void recordLogin(String accountId, HttpServletRequest request) {
        Member member = memberRepository.findByAccountIdAndStatusNot(accountId, MemberStatus.DELETED)
                .orElseThrow(() -> new SecurityException(SecurityErrorCode.UNAUTHORIZED));
        LoginHistory history = LoginHistory.builder()
                .member(member)
                .loginAt(LocalDateTime.now())
                .ipAddress(request.getRemoteAddr())
                .userAgent(request.getHeader("User-Agent"))
                .build();
        loginHistoryRepository.save(history);
    }

    /**
     * 페이징된 로그인 이력 조회
     */
    @Transactional(readOnly = true)
    public Page<LoginHistoryResponse> getLoginHistory(Long memberId, Pageable pageable) {
        return loginHistoryRepository.findByMember_MemberId(memberId, pageable)
                .map(this::toDto);
    }

    private LoginHistoryResponse toDto(LoginHistory h) {
        return LoginHistoryResponse.builder()
                .loginAt(h.getLoginAt())
                .ipAddress(h.getIpAddress())
                .userAgent(h.getUserAgent())
                .build();
    }
}
