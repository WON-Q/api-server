package com.fisa.wonq.merchant.repository;

import com.fisa.wonq.merchant.domain.Merchant;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MerchantRepository extends JpaRepository<Merchant, Long> {
    /**
     * (A) 매장 내 모든 테이블·주문·주문메뉴·옵션까지 한 번에 페치할 때 사용
     */
    @EntityGraph(attributePaths = {
            "tables",
            "tables.orders",
            "tables.orders.orderMenus",
            "tables.orders.orderMenus.orderMenuOptions"
    })
    Optional<Merchant> findWithTablesAndOrdersByMemberMemberId(Long memberId);

    /**
     * (B) 메뉴 등록·수정 등 단순 매장 조회 시 사용 (EntityGraph 없음)
     */
    Optional<Merchant> findByMemberMemberId(Long memberId);
}

