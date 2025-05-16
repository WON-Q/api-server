package com.fisa.wonq.order.domain;

import com.fisa.wonq.global.domain.BaseDateTimeEntity;
import com.fisa.wonq.merchant.domain.DiningTable;
import com.fisa.wonq.order.domain.enums.OrderStatus;
import com.fisa.wonq.order.domain.enums.PaymentMethod;
import com.fisa.wonq.order.domain.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 주문
 */
@Entity
@Table(name = "order")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Order extends BaseDateTimeEntity {

    // 1) 1씩 증가하는 숫자 PK
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 2) 클라이언트/PG 표시용 유니크 코드
    @Column(name = "order_code", nullable = false, updatable = false, unique = true)
    private String orderCode;

    @Column(nullable = false)
    private Integer totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus orderStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus paymentStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod paymentMethod;

    private LocalDateTime paidAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dining_table_id", nullable = false)
    private DiningTable diningTable;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OrderMenu> orderMenus = new ArrayList<>();

    /**
     * 양방향 편의 메서드
     **/
    public void addOrderMenu(OrderMenu om) {
        orderMenus.add(om);
        om.setOrder(this);
    }
}
