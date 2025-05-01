package com.fisa.wonq.order.repository;

import com.fisa.wonq.order.domain.Order;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    // orderCode 로 조회가 필요한 경우
    Optional<Order> findByOrderCode(String orderCode);

    // 주문 → 주문메뉴 → 주문메뉴옵션 → 메뉴, 메뉴옵션까지 한 번에 페치
    @EntityGraph(attributePaths = {
            "orderMenus",
            "orderMenus.menu",
            "orderMenus.orderMenuOptions",
            "orderMenus.orderMenuOptions.menuOption",
            "diningTable"
    })
    List<Order> findByDiningTable_Merchant_Member_MemberIdAndCreatedAtBetween(
            Long memberId,
            LocalDateTime from,
            LocalDateTime to
    );

    default List<Order> findByMerchantAndCreatedAtBetween(Long memberId, LocalDateTime from, LocalDateTime to) {
        return findByDiningTable_Merchant_Member_MemberIdAndCreatedAtBetween(memberId, from, to);
    }
}
