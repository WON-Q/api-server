package com.fisa.wonq.merchant.repository;

import com.fisa.wonq.merchant.domain.QrCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QrCodeRepository extends JpaRepository<QrCode, Long> {
    List<QrCode> findAllByMerchant_Member_MemberId(Long memberId);
}
