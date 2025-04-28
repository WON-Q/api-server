// com.fisa.wonq.member.service.MemberService.java
package com.fisa.wonq.member.service;

import com.fisa.wonq.member.controller.dto.MemberRequestDTO.SignupRequest;
import com.fisa.wonq.member.controller.dto.MemberResponseDTO;
import com.fisa.wonq.member.domain.Member;
import com.fisa.wonq.member.repository.MemberRepository;
import com.fisa.wonq.merchant.domain.Merchant;
import com.fisa.wonq.merchant.repository.MerchantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final MerchantRepository merchantRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public MemberResponseDTO.SignupResponse signup(SignupRequest req) {
        if (!isAccountIdAvailable(req.getAccountId())) {
            throw new IllegalArgumentException("이미 사용중인 아이디입니다.");
        }

        String encodedPwd = passwordEncoder.encode(req.getPassword());
        Member member = Member.of(req.getAccountId(), encodedPwd);

        Merchant merchant = Merchant.builder()
                .businessRegistrationNo(req.getBusinessRegistrationNo())
                .merchantName(req.getMerchantName())
                .merchantOwnerName(req.getMerchantOwnerName())
                .merchantOwnerPhoneNo(req.getMerchantOwnerPhoneNo())
                .merchantEmail(req.getMerchantEmail())
                .businessLaunchingDate(req.getBusinessLaunchingDate())
                .merchantAddress(req.getMerchantAddress())
                .merchantAccountBankName(req.getMerchantAccountBankName())
                .merchantAccount(req.getMerchantAccount())
                .merchantAccountHolderName(req.getMerchantAccountHolderName())
                .openTime(req.getOpenTime())
                .closeTime(req.getCloseTime())
                .build();

        // 2) 양방향 연관관계 세팅
        member.addMerchant(merchant);

        // cascade ALL 세팅 덕분에 member만 save 해도 merchant가 함께 저장됩니다.
        memberRepository.save(member);

        return MemberResponseDTO.SignupResponse.builder()
                .memberId(member.getMemberId())
                .accountId(member.getAccountId())
                .merchantId(merchant.getMerchantId())
                .build();
    }

    public boolean isAccountIdAvailable(String accountId) {
        return memberRepository.findByAccountId(accountId).isEmpty();
    }
}
