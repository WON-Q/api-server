package com.fisa.wonq.order.domain;

import com.fisa.wonq.global.domain.BaseDateTimeEntity;
import com.fisa.wonq.merchant.domain.MenuOption;
import jakarta.persistence.*;
import lombok.*;

/**
 * 주문-메뉴 옵션
 */
@Entity
@Table(name = "order_menu_option")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class OrderMenuOption extends BaseDateTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderMenuOptionId;

    @Column(nullable = false)
    private Integer optionPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_menu_id", nullable = false)
    private OrderMenu orderMenu;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_option_id", nullable = false)
    private MenuOption menuOption;

    /**
     * 부모(주문메뉴) 세팅
     **/
    public void setOrderMenu(OrderMenu orderMenu) {
        this.orderMenu = orderMenu;
    }
}
