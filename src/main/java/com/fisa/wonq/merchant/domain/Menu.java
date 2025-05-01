package com.fisa.wonq.merchant.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;

import java.util.ArrayList;
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

    @Column
    private String description;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private Integer price;

    @Lob
    private String menuImg;

    @Column(nullable = false)
    private Boolean isAvailable;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "merchant_id", nullable = false)
    private Merchant merchant;

    @OneToMany(mappedBy = "menu", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<MenuOptionGroup> optionGroups = new ArrayList<>();

    @OneToMany(mappedBy = "menu", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<MenuOption> options = new ArrayList<>();

    /**
     * 양방향 편의 메서드
     */
    public void setMerchant(Merchant merchant) {
        this.merchant = merchant;
    }

    public void addOptionGroup(MenuOptionGroup group) {
        optionGroups.add(group);
        group.setMenu(this);
    }

    // 판매 가능 여부 변경
    public void changeAvailability(Boolean available) {
        this.isAvailable = available;
    }
}
