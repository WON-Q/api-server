package com.fisa.wonq.merchant.repository;

import com.fisa.wonq.merchant.domain.Merchant;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MerchantRepository extends JpaRepository<Merchant, Long> {

    /**
     * 매장 내 모든 테이블만 한 번에 JOIN FETCH 하고,
     * 각 테이블의 orders는 @Fetch(SUBSELECT)에 의해 별도 쿼리로 로딩됩니다.
     */
    @EntityGraph(attributePaths = {
            "tables"
    })
    Optional<Merchant> findWithTablesAndOrdersByMemberMemberId(Long memberId);

    /**
     * 메뉴 등록·수정 등 단순 매장 조회 시 사용 (EntityGraph 없음)
     */
    Optional<Merchant> findByMemberMemberId(Long memberId);

    /**
     * 점주용: memberId 로 조회
     */
    @EntityGraph(attributePaths = {
            "menus",
            "menus.optionGroups",
            "menus.optionGroups.options"
    })
    Optional<Merchant> findWithMenusByMemberMemberId(Long memberId);

    /**
     * 비회원용: merchantId 로 조회
     */
    @EntityGraph(attributePaths = {
            "menus",
            "menus.optionGroups",
            "menus.optionGroups.options"
    })
    Optional<Merchant> findWithMenusByMerchantId(Long merchantId);
}

