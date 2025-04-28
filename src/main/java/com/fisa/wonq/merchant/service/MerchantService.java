package com.fisa.wonq.merchant.service;


import com.fisa.wonq.merchant.controller.dto.MerchantInfoResponse;
import com.fisa.wonq.merchant.domain.Merchant;
import com.fisa.wonq.merchant.exception.MerchantErrorCode;
import com.fisa.wonq.merchant.exception.MerchantException;
import com.fisa.wonq.merchant.repository.MerchantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MerchantService {

    private final MerchantRepository merchantRepository;

    @Transactional(readOnly = true)
    public MerchantInfoResponse getMerchantInfo(Long memberId) {
        Merchant m = merchantRepository
                .findByMemberMemberId(memberId)
                .orElseThrow(() -> new MerchantException(MerchantErrorCode.MERCHANT_NOT_FOUND));

        return MerchantInfoResponse.builder()
                .merchantName(m.getMerchantName())
                .businessRegistrationNo(m.getBusinessRegistrationNo())
                .merchantOwnerName(m.getMerchantOwnerName())
                .merchantOwnerPhoneNo(m.getMerchantOwnerPhoneNo())
                .merchantAddress(m.getMerchantAddress())
                .description(m.getDescription())
                .merchantAccountBankName(m.getMerchantAccountBankName())
                .merchantAccount(m.getMerchantAccount())
                .merchantAccountHolderName(m.getMerchantAccountHolderName())
                .build();
    }
}
