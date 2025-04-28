package com.fisa.wonq.merchant.domain;

import com.fisa.wonq.order.domain.OrderMenuOption;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 메뉴 개별 옵션
 */
@Entity
@Table(name = "menu_option")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class MenuOption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long menuOptionId;

    @Column(nullable = false)
    private String optionName;

    @Column(nullable = false)
    private Integer optionPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_option_group_id")
    private MenuOptionGroup optionGroup;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id", nullable = false)
    private Menu menu;

    @OneToMany(mappedBy = "menuOption", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OrderMenuOption> orderMenuOptions = new ArrayList<>();

    /**
     * 양방향 편의 메서드
     */
    public void setOptionGroup(MenuOptionGroup group) {
        this.optionGroup = group;
    }

    public void setMenu(Menu menu) {
        this.menu = menu;
    }
}
