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
import com.fisa.wonq.order.domain.PaymentResult;
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
    private final PaymentService paymentService;

    /**
     * 주문 생성(결제 요청)
     */
    @Transactional
    public OrderResponse createOrder(OrderRequest req) {
        // 1. 테이블 조회 및 상태 변경
        DiningTable table = tableRepo.findById(req.getTableId())
                .orElseThrow(() -> new MerchantException(MerchantErrorCode.TABLE_NOT_FOUND));
        table.changeStatus(TableStatus.IN_PROGRESS);

        // 2. 주문 ID 생성
        String orderId = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyMMdd'T'HHmm"))
                + "_t" + table.getTableNumber();

        // 3. PG 결제 요청 (스텁)
        PaymentResult payResult = paymentService.charge(
                orderId,
                req.getTotalAmount(),
                req.getPaymentMethod()
        );
        // TODO: 세부적인 결제 에러 핸들링 필요(스텁 단계에서는 일단 String으로 처리
        if (!payResult.isSuccess()) {
            throw new IllegalStateException("PAYMENT_FAILED");
        }

        // 4. Order 엔티티 생성 (결제 완료 정보 반영)
        LocalDateTime now = LocalDateTime.now();
        Order order = Order.builder()
                .orderId(orderId)
                .totalAmount(req.getTotalAmount())
                .orderStatus(com.fisa.wonq.order.domain.enums.OrderStatus.PAID)
                .paymentStatus(com.fisa.wonq.order.domain.enums.PaymentStatus.COMPLETED)
                .paymentMethod(req.getPaymentMethod())
                .paidAt(payResult.getPaidAt())
                .createdAt(now)
                .updatedAt(now)
                .diningTable(table)
                .build();

        // 5. 메뉴별 OrderMenu, OrderMenuOption 생성
        for (var m : req.getMenus()) {
            Menu menu = menuRepo.findById(m.getMenuId())
                    .orElseThrow(() -> new MenuException(MenuErrorCode.MENU_NOT_FOUND);

            OrderMenu om = OrderMenu.builder()
                    .menu(menu)
                    .quantity(m.getQuantity())
                    .unitPrice(menu.getPrice())
                    .totalPrice(menu.getPrice() * m.getQuantity())
                    .build();

            if (m.getOptionIds() != null) {
                for (Long optId : m.getOptionIds()) {
                    MenuOption mo = menuOptionRepo.findById(optId)
                            .orElseThrow(() -> new MerchantException(MerchantErrorCode.OPTION_NOT_FOUND);
                    OrderMenuOption omo = OrderMenuOption.builder()
                            .menuOption(mo)
                            .optionPrice(mo.getOptionPrice())
                            .build();
                    om.addOption(omo);
                    om.setTotalPrice(om.getTotalPrice() + mo.getOptionPrice());
                }
            }
            order.addOrderMenu(om);
        }

        // 6. 저장
        orderRepo.save(order);

        // 7. 응답
        return OrderResponse.builder()
                .orderId(order.getOrderId())
                .totalAmount(order.getTotalAmount())
                .paymentTransactionId(payResult.getTransactionId())
                .build();
    }
}
