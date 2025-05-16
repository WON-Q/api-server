package com.fisa.wonq.merchant.repository;

import com.fisa.wonq.merchant.domain.DiningTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DiningTableRepository extends JpaRepository<DiningTable, Long> {

    /**
     * memberId 기준으로 소속 가맹점까지 조인해서 테이블을 한 번에 조회합니다.
     * 없으면 Optional.empty().
     */
    Optional<DiningTable> findByDiningTableIdAndMerchant_Member_MemberId(Long tableId, Long memberId);

    Optional<DiningTable> findByDiningTableId(Long diningTableId);

    boolean existsByMerchant_Member_MemberIdAndTableNumber(Long memberId, Integer tableNumber);
}
