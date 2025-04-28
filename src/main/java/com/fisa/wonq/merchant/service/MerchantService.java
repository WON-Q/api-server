package com.fisa.wonq.merchant.service;


import com.fisa.wonq.global.security.resolver.Account;
import com.fisa.wonq.merchant.controller.dto.DiningTableDetailResponse;
import com.fisa.wonq.merchant.controller.dto.DiningTableRequest;
import com.fisa.wonq.merchant.controller.dto.DiningTableResponse;
import com.fisa.wonq.merchant.controller.dto.MerchantInfoResponse;
import com.fisa.wonq.merchant.domain.DiningTable;
import com.fisa.wonq.merchant.domain.Merchant;
import com.fisa.wonq.merchant.exception.MerchantErrorCode;
import com.fisa.wonq.merchant.exception.MerchantException;
import com.fisa.wonq.merchant.repository.DiningTableRepository;
import com.fisa.wonq.merchant.repository.MerchantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MerchantService {

    private final MerchantRepository merchantRepository;
    private final DiningTableRepository diningTableRepository;

    @Transactional(readOnly = true)
    public MerchantInfoResponse getMerchantInfo(Long memberId) {
        Merchant m = merchantRepository
                .findByMemberMemberId(memberId)
                .orElseThrow(() -> new MerchantException(MerchantErrorCode.MERCHANT_NOT_FOUND));

        return MerchantInfoResponse.builder()
                .merchantName(m.getMerchantName())
                .businessRegistrationNo(m.getBusinessRegistrationNo())
                .merchantOwnerName(m.getMerchantOwnerName())
                .merchantOwnerPhoneNo(m.getMerchantOwnerPhoneNo())
                .merchantAddress(m.getMerchantAddress())
                .description(m.getDescription())
                .merchantAccountBankName(m.getMerchantAccountBankName())
                .merchantAccount(m.getMerchantAccount())
                .merchantAccountHolderName(m.getMerchantAccountHolderName())
                .build();
    }


    @Transactional
    public DiningTableResponse addDiningTable(Account account, DiningTableRequest req) {
        // 현재 로그인한 회원의 매장 찾기
        Merchant merchant = merchantRepository
                .findByMemberMemberId(account.id())
                .orElseThrow(() -> new IllegalArgumentException("해당 회원의 매장을 찾을 수 없습니다."));

        // 테이블 엔티티 생성
        DiningTable table = DiningTable.builder()
                .merchant(merchant)
                .tableNumber(req.getTableNumber())
                .capacity(req.getCapacity())
                .status(req.getStatus())
                .locationX(req.getLocationX())
                .locationY(req.getLocationY())
                .locationW(req.getLocationW())
                .locationH(req.getLocationH())
                .build();

        // 저장
        diningTableRepository.save(table);

        return new DiningTableResponse(table.getDiningTableId());
    }

    @Transactional(readOnly = true)
    public List<DiningTableDetailResponse> getDiningTablesWithOrders(Long memberId) {
        var merchant = merchantRepository
                .findByMemberMemberId(memberId)
                .orElseThrow(() -> new MerchantException(MerchantErrorCode.MERCHANT_NOT_FOUND));

        return merchant.getTables().stream()
                .map(table -> {
                    var tableDto = DiningTableDetailResponse.builder()
                            .diningTableId(table.getDiningTableId())
                            .tableNumber(table.getTableNumber())
                            .capacity(table.getCapacity())
                            .status(table.getStatus())
                            .locationX(table.getLocationX())
                            .locationY(table.getLocationY())
                            .locationW(table.getLocationW())
                            .locationH(table.getLocationH())
                            .build();

                    var orders = table.getOrders().stream()
                            .map(order -> {
                                var orderDto = DiningTableDetailResponse.OrderResponse.builder()
                                        .orderId(order.getOrderId())
                                        .totalAmount(order.getTotalAmount())
                                        .orderStatus(order.getOrderStatus())
                                        .createdAt(order.getCreatedAt())
                                        .build();

                                var menus = order.getOrderMenus().stream()
                                        .map(om -> {
                                            var menu = om.getMenu();
                                            var omDto = DiningTableDetailResponse.OrderMenuResponse.builder()
                                                    .orderMenuId(om.getOrderMenuId())
                                                    .menuId(menu.getMenuId())
                                                    .menuName(menu.getName())
                                                    .quantity(om.getQuantity())
                                                    .unitPrice(om.getUnitPrice())
                                                    .totalPrice(om.getTotalPrice())
                                                    .build();

                                            var opts = om.getOrderMenuOptions().stream()
                                                    .map(opt -> DiningTableDetailResponse.OrderMenuOptionResponse.builder()
                                                            .orderMenuOptionId(opt.getOrderMenuOptionId())
                                                            .menuOptionId(opt.getMenuOption().getMenuOptionId())
                                                            .optionName(opt.getMenuOption().getOptionName())
                                                            .optionPrice(opt.getOptionPrice())
                                                            .build()
                                                    ).toList();

                                            omDto.setOptions(opts);
                                            return omDto;
                                        }).toList();

                                orderDto.setOrderMenus(menus);
                                return orderDto;
                            }).toList();

                    tableDto.setOrders(orders);
                    return tableDto;
                }).toList();
    }
}
