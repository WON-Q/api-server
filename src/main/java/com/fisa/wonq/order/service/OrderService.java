package com.fisa.wonq.order.service;

import com.fisa.wonq.merchant.domain.DiningTable;
import com.fisa.wonq.merchant.domain.Menu;
import com.fisa.wonq.merchant.domain.MenuOption;
import com.fisa.wonq.merchant.domain.enums.TableStatus;
import com.fisa.wonq.merchant.exception.MenuErrorCode;
import com.fisa.wonq.merchant.exception.MenuException;
import com.fisa.wonq.merchant.exception.MerchantErrorCode;
import com.fisa.wonq.merchant.exception.MerchantException;
import com.fisa.wonq.merchant.repository.DiningTableRepository;
import com.fisa.wonq.merchant.repository.MenuOptionRepository;
import com.fisa.wonq.merchant.repository.MenuRepository;
import com.fisa.wonq.order.controller.dto.req.OrderRequest;
import com.fisa.wonq.order.controller.dto.res.OrderResponse;
import com.fisa.wonq.order.domain.Order;
import com.fisa.wonq.order.domain.OrderMenu;
import com.fisa.wonq.order.domain.OrderMenuOption;
import com.fisa.wonq.order.domain.enums.OrderStatus;
import com.fisa.wonq.order.domain.enums.PaymentStatus;
import com.fisa.wonq.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final DiningTableRepository tableRepo;
    private final MenuRepository menuRepo;
    private final MenuOptionRepository menuOptionRepo;
    private final OrderRepository orderRepo;

    @Transactional
    public OrderResponse createOrder(OrderRequest req) {
        // 테이블 조회 및 상태 변경
        DiningTable table = tableRepo.findById(req.getTableId())
                .orElseThrow(() -> new MerchantException(MerchantErrorCode.TABLE_NOT_FOUND));
        table.setStatus(TableStatus.IN_PROGRESS);

        // 주문 ID 생성 (yyMMdd'T'HHmm_t{tableNumber})
        String orderId = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMdd'T'HHmm"))
                + "_t" + table.getTableNumber();

        // Order 엔티티 생성
        LocalDateTime now = LocalDateTime.now();
        Order order = Order.builder()
                .orderId(orderId)
                .totalAmount(req.getTotalAmount())
                .orderStatus(OrderStatus.PAID)
                .paymentStatus(PaymentStatus.COMPLETED)
                .paymentMethod(req.getPaymentMethod())
                .paidAt(now)
                .createdAt(now)
                .updatedAt(now)
                .diningTable(table)
                .build();

        // 메뉴별 OrderMenu, OrderMenuOption 생성
        for (var m : req.getMenus()) {
            Menu menu = menuRepo.findById(m.getMenuId())
                    .orElseThrow(() -> new MenuException(MenuErrorCode.MENU_NOT_FOUND));

            OrderMenu om = OrderMenu.builder()
                    .menu(menu)
                    .quantity(m.getQuantity())
                    .unitPrice(menu.getPrice())
                    .totalPrice(menu.getPrice() * m.getQuantity())
                    .build();

            if (m.getOptionIds() != null) {
                for (Long optId : m.getOptionIds()) {
                    MenuOption mo = menuOptionRepo.findById(optId)
                            .orElseThrow(() -> new MerchantException(MerchantErrorCode.OPTION_NOT_FOUND));
                    OrderMenuOption omo = OrderMenuOption.builder()
                            .menuOption(mo)
                            .optionPrice(mo.getOptionPrice())
                            .build();
                    om.addOption(omo);
                    // 옵션 가격만큼 totalPrice에 추가
                    om.setTotalPrice(om.getTotalPrice() + mo.getOptionPrice());
                }
            }

            order.addOrderMenu(om);
        }

        // 저장 (테이블 상태와 주문 모두 트랜잭션에 반영)
        orderRepo.save(order);

        // 응답
        return OrderResponse.builder()
                .orderId(order.getOrderId())
                .totalAmount(order.getTotalAmount())
                .build();
    }
}
