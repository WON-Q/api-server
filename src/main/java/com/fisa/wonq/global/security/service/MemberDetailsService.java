package com.fisa.wonq.global.security.service;

import com.fisa.wonq.global.security.exception.SecurityException;
import com.fisa.wonq.global.security.user.UserDetailsImpl;
import com.fisa.wonq.member.domain.Member;
import com.fisa.wonq.member.domain.enums.MemberStatus;
import com.fisa.wonq.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


import static com.fisa.wonq.member.exception.MemberErrorCode.MEMBER_ALREADY_DELETED;
import static com.fisa.wonq.member.exception.MemberErrorCode.MEMBER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class MemberDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        Member member = memberRepository.findByAccountIdAndStatusNot(userId, MemberStatus.DELETED)
                .orElseThrow(() -> new SecurityException(MEMBER_NOT_FOUND));

        if (member.isDeleted()) {
            throw new SecurityException(MEMBER_ALREADY_DELETED);
        }

        List<GrantedAuthority> authorities = getAuthorities(member);

        // 기본 ROLE_USER 권한 추가
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

        return UserDetailsImpl.builder()
                .id(member.getMemberId())
                .userId(member.getAccountId())
                .password(member.getPassword())
                .authorities(authorities)
                .build();
    }

    private List<GrantedAuthority> getAuthorities(Member member) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        if (member.getRole() != null) {
            authorities.add(new SimpleGrantedAuthority(member.getRole().name()));
        }
        return authorities;
    }
}
