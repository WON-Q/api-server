package com.fisa.wonq.merchant.service;

import com.fisa.wonq.global.security.resolver.Account;
import com.fisa.wonq.merchant.controller.dto.DiningTableRequest;
import com.fisa.wonq.merchant.controller.dto.DiningTableResponse;
import com.fisa.wonq.merchant.domain.DiningTable;
import com.fisa.wonq.merchant.domain.Merchant;
import com.fisa.wonq.merchant.repository.DiningTableRepository;
import com.fisa.wonq.merchant.repository.MerchantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DiningTableService {

    private final MerchantRepository merchantRepository;
    private final DiningTableRepository diningTableRepository;

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
}
