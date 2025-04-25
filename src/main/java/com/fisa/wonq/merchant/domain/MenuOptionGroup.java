package com.fisa.wonq.merchant.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

/**
 * 메뉴 추가 옵션 그룹
 */
@Entity
@Table(name = "menu_option_group")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class MenuOptionGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long menuOptionGroupId;

    @Column(nullable = false)
    private String menuOptionGroupName;

    private Integer displaySequence;

    @Column(nullable = false)
    private Boolean isDefault;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id", nullable = false)
    private Menu menu;

    @OneToMany(mappedBy = "optionGroup", cascade = CascadeType.ALL)
    private List<MenuOption> options;
}
