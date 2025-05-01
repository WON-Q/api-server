package com.fisa.wonq.order.domain;

import com.fisa.wonq.global.domain.BaseDateTimeEntity;
import com.fisa.wonq.merchant.domain.Menu;
import com.fisa.wonq.order.domain.enums.OrderMenuStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 주문-메뉴
 */
@Entity
@Table(name = "order_menu")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class OrderMenu extends BaseDateTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderMenuId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private OrderMenuStatus status = OrderMenuStatus.ORDERED;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private Integer unitPrice;

    @Column(nullable = false)
    private Integer totalPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id", nullable = false)
    private Menu menu;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @OneToMany(mappedBy = "orderMenu", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OrderMenuOption> orderMenuOptions = new ArrayList<>();

    /**
     * 양방향 편의 메서드
     **/
    public void setOrder(Order order) {
        this.order = order;
    }

    public void addOption(OrderMenuOption opt) {
        orderMenuOptions.add(opt);
        opt.setOrderMenu(this);
    }

    public void setTotalPrice(Integer totalPrice) {
        this.totalPrice = totalPrice;
    }
}
