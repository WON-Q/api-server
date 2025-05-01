package com.fisa.wonq.merchant.repository;

import com.fisa.wonq.merchant.domain.Menu;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuRepository extends JpaRepository<Menu, Long> {

    /**
     * 옵션 그룹만 한 번에 페치합니다.
     * 옵션(optionGroups.options)은 지연 로딩으로 처리해서 다중 BagFetch 예외를 피합니다.
     */
    @EntityGraph(attributePaths = {"optionGroups"})
    List<Menu> findAllByMerchant_MerchantId(Long merchantId);
}
