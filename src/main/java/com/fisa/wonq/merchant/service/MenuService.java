package com.fisa.wonq.merchant.service;

import com.fisa.wonq.merchant.controller.dto.req.MenuRequest;
import com.fisa.wonq.merchant.controller.dto.res.MenuDetailResponse;
import com.fisa.wonq.merchant.controller.dto.res.MenuResponse;
import com.fisa.wonq.merchant.domain.Menu;
import com.fisa.wonq.merchant.domain.MenuOption;
import com.fisa.wonq.merchant.domain.MenuOptionGroup;
import com.fisa.wonq.merchant.domain.Merchant;
import com.fisa.wonq.merchant.exception.MerchantErrorCode;
import com.fisa.wonq.merchant.exception.MerchantException;
import com.fisa.wonq.merchant.repository.MenuRepository;
import com.fisa.wonq.merchant.repository.MerchantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MenuService {

    private final MerchantRepository merchantRepository;
    private final MenuRepository menuRepository;

    // 메뉴 등록 및 추가
    @Transactional
    public MenuResponse createMenu(Long memberId, MenuRequest req) {
        // 매장 조회
        Merchant merchant = merchantRepository
                .findByMemberMemberId(memberId)
                .orElseThrow(() -> new MerchantException(MerchantErrorCode.MERCHANT_NOT_FOUND));

        // Menu 엔티티 생성 (builder 로는 FK 없이 생성)
        Menu menu = Menu.builder()
                .name(req.getName())
                .description(req.getDescription())
                .category(req.getCategory())
                .price(req.getPrice())
                .menuImg(req.getMenuImgUrl())
                .isAvailable(req.getIsAvailable())
                .build();

        // 양방향 편의 메서드로 merchant ↔ menu 연결
        merchant.addMenu(menu);

        // 옵션 그룹 + 옵션들 매핑
        if (req.getOptionGroups() != null) {
            int seq = 0;
            for (MenuRequest.OptionGroupRequest g : req.getOptionGroups()) {
                // 그룹 생성 (menu FK 없이)
                MenuOptionGroup group = MenuOptionGroup.builder()
                        .menuOptionGroupName(g.getGroupName())
                        .displaySequence(seq)
                        .isDefault(seq == 0)
                        .build();
                // 편의 메서드로 menu ↔ group 연결
                menu.addOptionGroup(group);

                // 옵션들 매핑
                if (g.getOptions() != null) {
                    for (MenuRequest.OptionRequest o : g.getOptions()) {
                        MenuOption opt = MenuOption.builder()
                                .optionName(o.getOptionName())
                                .optionPrice(o.getOptionPrice())
                                .build();
                        // 편의 메서드로 group ↔ option 연결
                        group.addOption(opt);
                    }
                }
                seq++;
            }
        }

        // save 한 번으로 cascade ALL 된 group, option 모두 저장
        Menu saved = menuRepository.save(menu);

        // 결과 반환
        return MenuResponse.builder()
                .menuId(saved.getMenuId())
                .build();
    }

    // 매장 내 전체 메뉴 조회
    @Transactional(readOnly = true)
    public List<MenuDetailResponse> getMenusWithOptionsByMerchantId(Long merchantId) {
        List<Menu> menus = menuRepository
                .findAllByMerchant_MerchantId(merchantId);

        return menus.stream()
                .map(menu -> {
                    // 옵션 그룹 → DTO 변환
                    var groups = menu.getOptionGroups().stream()
                            .map(g -> {
                                // 옵션은 지연 로딩: 필요한 시점에 쿼리 실행
                                var optionDtos = g.getOptions().stream()
                                        .map(o -> MenuDetailResponse.Option.builder()
                                                .optionId(o.getMenuOptionId())
                                                .optionName(o.getOptionName())
                                                .optionPrice(o.getOptionPrice())
                                                .build())
                                        .toList();

                                return MenuDetailResponse.OptionGroup.builder()
                                        .groupId(g.getMenuOptionGroupId())
                                        .groupName(g.getMenuOptionGroupName())
                                        .displaySequence(g.getDisplaySequence())
                                        .isDefault(g.getIsDefault())
                                        .options(optionDtos)
                                        .build();
                            })
                            .toList();

                    // 메뉴 → DTO 변환
                    return MenuDetailResponse.builder()
                            .menuId(menu.getMenuId())
                            .name(menu.getName())
                            .description(menu.getDescription())
                            .category(menu.getCategory())
                            .price(menu.getPrice())
                            .menuImgUrl(menu.getMenuImg())
                            .isAvailable(menu.getIsAvailable())
                            .optionGroups(groups)
                            .build();
                })
                .toList();
    }
}
