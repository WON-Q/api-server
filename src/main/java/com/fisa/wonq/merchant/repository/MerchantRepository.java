package com.fisa.wonq.merchant.repository;

import com.fisa.wonq.merchant.domain.Merchant;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MerchantRepository extends JpaRepository<Merchant, Long> {
    /**
     * 테이블, 주문, 주문메뉴, 주문메뉴옵션까지 한 번에 페치해서 가져옵니다.
     */
    @EntityGraph(attributePaths = {
            "tables",
            "tables.orders",
            "tables.orders.orderMenus",
            "tables.orders.orderMenus.orderMenuOptions"
    })
    Optional<Merchant> findByMemberMemberId(Long memberId);
}
