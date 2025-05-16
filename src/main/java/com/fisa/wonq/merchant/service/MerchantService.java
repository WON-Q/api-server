package com.fisa.wonq.merchant.service;


import com.fisa.wonq.global.security.resolver.Account;
import com.fisa.wonq.merchant.controller.dto.req.DiningTableRequest;
import com.fisa.wonq.merchant.controller.dto.req.DiningTableStatusRequest;
import com.fisa.wonq.merchant.controller.dto.req.DiningTableUpdateRequest;
import com.fisa.wonq.merchant.controller.dto.req.MerchantInfoUpdateRequest;
import com.fisa.wonq.merchant.controller.dto.res.*;
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
                .merchantId(m.getMerchantId())
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
                                        .orderId(order.getId())
                                        .orderCode(order.getOrderCode())
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

    // 테이블 상태 변경
    @Transactional
    public DiningTableStatusResponse resetTableStatus(Long memberId, Long tableId, DiningTableStatusRequest req) {
        DiningTable table = diningTableRepository
                .findByDiningTableIdAndMerchant_Member_MemberId(tableId, memberId)
                .orElseThrow(() -> new MerchantException(MerchantErrorCode.MERCHANT_NOT_FOUND));

        table.changeStatus(req.getStatus());

        return DiningTableStatusResponse.builder()
                .diningTableId(table.getDiningTableId())
                .status(table.getStatus())
                .build();
    }

    // 가맹점 정보 업데이트
    @Transactional
    public MerchantInfoResponse updateMerchantInfo(Long memberId, MerchantInfoUpdateRequest req) {
        Merchant m = merchantRepository.findByMemberMemberId(memberId)
                .orElseThrow(() -> new MerchantException(MerchantErrorCode.MERCHANT_NOT_FOUND));

        // 변경된 필드만 엔티티에 반영
        m.updateBasicInfo(
                req.getMerchantOwnerPhoneNo(),
                req.getDescription(),
                req.getMerchantAccountBankName(),
                req.getMerchantAccount(),
                req.getMerchantAccountHolderName()
        );

        // JPA 더티체킹으로 자동 반영
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

    // 테이블 정보 수정
    @Transactional
    public DiningTableUpdateResponse updateDiningTableInfo(
            Long memberId,
            Long tableId,
            DiningTableUpdateRequest req
    ) {
        // 권한이 확인된 테이블만 조회
        DiningTable table = diningTableRepository
                .findByDiningTableIdAndMerchant_Member_MemberId(tableId, memberId)
                .orElseThrow(() -> new MerchantException(MerchantErrorCode.TABLE_NOT_FOUND));

        // 변경된 값만 덮어쓰기
        table.updateInfo(req.getTableNumber(), req.getCapacity());

        // JPA 더티체킹으로 자동 저장
        return DiningTableUpdateResponse.builder()
                .diningTableId(table.getDiningTableId())
                .tableNumber(table.getTableNumber())
                .capacity(table.getCapacity())
                .build();
    }

    @Transactional(readOnly = true)
    public MerchantOverviewResponse getMerchantOverview(Long merchantId) {
        Merchant m = merchantRepository.findById(merchantId)
                .orElseThrow(() -> new MerchantException(MerchantErrorCode.MERCHANT_NOT_FOUND));

        return MerchantOverviewResponse.builder()
                .merchantId(m.getMerchantId())
                .merchantName(m.getMerchantName())
                .merchantImgUrl(m.getMerchantImg())
                .build();
    }
}
