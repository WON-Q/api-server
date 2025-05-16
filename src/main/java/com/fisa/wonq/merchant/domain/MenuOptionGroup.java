package com.fisa.wonq.merchant.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.util.ArrayList;
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

    @OneToMany(mappedBy = "optionGroup", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @Fetch(FetchMode.SUBSELECT)
    private List<MenuOption> options = new ArrayList<>();

    /**
     * 양방향 편의 메서드
     */
    public void setMenu(Menu menu) {
        this.menu = menu;
    }

    public void addOption(MenuOption option) {
        options.add(option);
        option.setOptionGroup(this);
        option.setMenu(this.menu);
    }
}
