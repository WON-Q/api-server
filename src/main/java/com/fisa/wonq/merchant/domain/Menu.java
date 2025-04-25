package com.fisa.wonq.merchant.domain;

import com.fisa.wonq.order.domain.OrderMenu;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;

import java.util.List;

/**
 * 메뉴
 */
@Entity
@Table(name = "menu")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@DynamicInsert
public class Menu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long menuId;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(nullable = false)
    private Integer price;

    @Lob
    private String menuImg;

    @Column(nullable = false)
    private Boolean isAvailable;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "merchant_id", nullable = false)
    private Merchant merchant;

    @OneToMany(mappedBy = "menu", cascade = CascadeType.ALL)
    private List<MenuOptionGroup> optionGroups;

    @OneToMany(mappedBy = "menu", cascade = CascadeType.ALL)
    private List<MenuOption> options;

    @OneToMany(mappedBy = "menu", cascade = CascadeType.ALL)
    private List<OrderMenu> orderMenus;
}
