package com.fisa.wonq.merchant.service;


import com.fisa.wonq.global.security.resolver.Account;
import com.fisa.wonq.merchant.controller.dto.req.DiningTableRequest;
import com.fisa.wonq.merchant.controller.dto.res.DiningTableDetailResponse;
import com.fisa.wonq.merchant.controller.dto.res.DiningTableResponse;
import com.fisa.wonq.merchant.controller.dto.res.MerchantImageResponse;
import com.fisa.wonq.merchant.controller.dto.res.MerchantInfoResponse;
import com.fisa.wonq.merchant.domain.DiningTable;
import com.fisa.wonq.merchant.domain.Merchant;
import com.fisa.wonq.merchant.exception.MerchantErrorCode;
import com.fisa.wonq.merchant.exception.MerchantException;
import com.fisa.wonq.merchant.repository.DiningTableRepository;
import com.fisa.wonq.merchant.repository.MerchantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MerchantService {

    private final MerchantRepository merchantRepository;
    private final DiningTableRepository diningTableRepository;
    private final S3UploadService s3UploadService;

    // 가맹점 정보 조회
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


    // 테이블 추가
    @Transactional
    public DiningTableResponse addDiningTable(Account account, DiningTableRequest req) {
        // 현재 로그인한 회원의 매장 찾기
        Merchant merchant = merchantRepository
                .findByMemberMemberId(account.id())
                .orElseThrow(() -> new MerchantException(MerchantErrorCode.MERCHANT_NOT_FOUND));

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

    // 주문 내역과 함께 테이블 조회
    @Transactional(readOnly = true)
    public List<DiningTableDetailResponse> getDiningTablesWithOrders(Long memberId) {
        Merchant merchant = merchantRepository
                .findWithTablesAndOrdersByMemberMemberId(memberId)
                .orElseThrow(() -> new MerchantException(MerchantErrorCode.MERCHANT_NOT_FOUND));

        return merchant.getTables().stream()
                .map(table -> {
                    // 주문 DTO
                    List<DiningTableDetailResponse.OrderResponse> orders = table.getOrders().stream()
                            .map(order -> {
                                // 주문메뉴 DTO
                                List<DiningTableDetailResponse.OrderMenuResponse> menus =
                                        order.getOrderMenus().stream()
                                                .map(om -> {
                                                    var omDto = DiningTableDetailResponse.OrderMenuResponse.builder()
                                                            .orderMenuId(om.getOrderMenuId())
                                                            .menuId(om.getMenu().getMenuId())
                                                            .menuName(om.getMenu().getName())
                                                            .quantity(om.getQuantity())
                                                            .unitPrice(om.getUnitPrice())
                                                            .totalPrice(om.getTotalPrice())
                                                            .build();
                                                    // 옵션 DTO
                                                    omDto.setOptions(om.getOrderMenuOptions().stream()
                                                            .map(opt -> DiningTableDetailResponse.OrderMenuOptionResponse.builder()
                                                                    .orderMenuOptionId(opt.getOrderMenuOptionId())
                                                                    .menuOptionId(opt.getMenuOption().getMenuOptionId())
                                                                    .optionName(opt.getMenuOption().getOptionName())
                                                                    .optionPrice(opt.getOptionPrice())
                                                                    .build())
                                                            .toList()
                                                    );
                                                    return omDto;
                                                })
                                                .toList();

                                // 주문 DTO
                                return DiningTableDetailResponse.OrderResponse.builder()
                                        .orderId(order.getOrderId())
                                        .totalAmount(order.getTotalAmount())
                                        .orderStatus(order.getOrderStatus())
                                        .createdAt(order.getCreatedAt())
                                        .orderMenus(menus)
                                        .build();
                            })
                            .toList();

                    // 테이블 DTO
                    return DiningTableDetailResponse.builder()
                            .diningTableId(table.getDiningTableId())
                            .tableNumber(table.getTableNumber())
                            .capacity(table.getCapacity())
                            .status(table.getStatus())
                            .locationX(table.getLocationX())
                            .locationY(table.getLocationY())
                            .locationW(table.getLocationW())
                            .locationH(table.getLocationH())
                            .orders(orders)
                            .build();
                })
                .toList();
    }

    // 이미지 업로드
    @Transactional
    public MerchantImageResponse uploadMerchantImage(MultipartFile file) throws IOException {

        String imageUrl = s3UploadService.upload(file);

        return new MerchantImageResponse(imageUrl);
    }
}
