package com.fisa.wonq.merchant.service;

import com.fisa.wonq.merchant.domain.Merchant;
import com.fisa.wonq.merchant.domain.QrCode;
import com.fisa.wonq.merchant.exception.MerchantErrorCode;
import com.fisa.wonq.merchant.exception.MerchantException;
import com.fisa.wonq.merchant.repository.MerchantRepository;
import com.fisa.wonq.merchant.repository.QrCodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QrCodeService {

    private final QrCodeRepository qrCodeRepository;
    private final MerchantRepository merchantRepository;

    /**
     * QR 생성 시 호출—이미지를 S3에 올린 뒤 호출
     */
    @Transactional
    public QrCode saveQrCode(Long memberId, String targetUrl, String imageUrl) {
        Merchant merchant = merchantRepository
                .findByMemberMemberId(memberId)
                .orElseThrow(() -> new MerchantException(MerchantErrorCode.MERCHANT_NOT_FOUND));

        QrCode qr = QrCode.builder()
                .merchant(merchant)
                .targetUrl(targetUrl)
                .imageUrl(imageUrl)
                .build();

        return qrCodeRepository.save(qr);
    }

    /**
     * 가맹점별 QR 목록 조회
     */
    @Transactional(readOnly = true)
    public List<QrCode> getQrCodes(Long memberId) {
        return qrCodeRepository.findAllByMerchant_Member_MemberId(memberId);
    }
}
