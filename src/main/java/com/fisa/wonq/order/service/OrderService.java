package com.fisa.wonq.order.service;

import com.fisa.wonq.merchant.domain.DiningTable;
import com.fisa.wonq.merchant.domain.Menu;
import com.fisa.wonq.merchant.domain.MenuOption;
import com.fisa.wonq.merchant.domain.Merchant;
import com.fisa.wonq.merchant.domain.enums.TableStatus;
import com.fisa.wonq.merchant.exception.MenuErrorCode;
import com.fisa.wonq.merchant.exception.MenuException;
import com.fisa.wonq.merchant.exception.MerchantErrorCode;
import com.fisa.wonq.merchant.exception.MerchantException;
import com.fisa.wonq.merchant.repository.DiningTableRepository;
import com.fisa.wonq.merchant.repository.MenuOptionRepository;
import com.fisa.wonq.merchant.repository.MenuRepository;
import com.fisa.wonq.order.controller.dto.req.OrderPrepareRequest;
import com.fisa.wonq.order.controller.dto.req.OrderRequest;
import com.fisa.wonq.order.controller.dto.res.OrderDetailResponse;
import com.fisa.wonq.order.controller.dto.res.OrderPrepareResponse;
import com.fisa.wonq.order.controller.dto.res.OrderResponse;
import com.fisa.wonq.order.domain.Order;
import com.fisa.wonq.order.domain.OrderMenu;
import com.fisa.wonq.order.domain.OrderMenuOption;
import com.fisa.wonq.order.domain.PaymentResult;
import com.fisa.wonq.order.domain.enums.OrderMenuStatus;
import com.fisa.wonq.order.domain.enums.OrderStatus;
import com.fisa.wonq.order.domain.enums.PaymentStatus;
import com.fisa.wonq.order.repository.OrderMenuRepository;
import com.fisa.wonq.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final DiningTableRepository tableRepo;
    private final MenuRepository menuRepo;
    private final MenuOptionRepository menuOptionRepo;
    private final OrderRepository orderRepo;
    private final OrderMenuRepository orderMenuRepo;
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
        String orderCode = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyMMdd'T'HHmm"))
                + "_t" + table.getTableNumber();

        // 3. PG 결제 요청 (스텁)
        PaymentResult payResult = paymentService.charge(
                orderCode,
                req.getTotalAmount(),
                req.getPaymentMethod()
        );
        // TODO: 세부적인 결제 에러 핸들링 필요(스텁 단계에서는 일단 String으로 처리)
        if (!payResult.isSuccess()) {
            throw new IllegalStateException("PAYMENT_FAILED");
        }

        // 4. Order 엔티티 생성 (결제 완료 정보 반영)
        Order order = Order.builder()
                .orderCode(orderCode)
                .totalAmount(req.getTotalAmount())
                .orderStatus(com.fisa.wonq.order.domain.enums.OrderStatus.PAID)
                .paymentStatus(com.fisa.wonq.order.domain.enums.PaymentStatus.COMPLETED)
                .paymentMethod(req.getPaymentMethod())
                .paidAt(payResult.getPaidAt())
                .diningTable(table)
                .build();

        // 5. 메뉴별 OrderMenu, OrderMenuOption 생성
        for (var m : req.getMenus()) {
            Menu menu = menuRepo.findById(m.getMenuId())
                    .orElseThrow(() -> new MenuException(MenuErrorCode.MENU_NOT_FOUND));

            OrderMenu om = OrderMenu.builder()
                    .menu(menu)
                    .quantity(m.getQuantity())
                    .unitPrice(menu.getPrice())
                    .totalPrice(menu.getPrice() * m.getQuantity())
                    .status(OrderMenuStatus.ORDERED)
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
                    om.setTotalPrice(om.getTotalPrice() + mo.getOptionPrice());
                }
            }
            order.addOrderMenu(om);
        }

        // 6. 저장
        orderRepo.save(order);

        // 7. 응답
        return OrderResponse.builder()
                .orderCode(order.getOrderCode())
                .totalAmount(order.getTotalAmount())
                .paymentTransactionId(payResult.getTransactionId())
                .build();
    }

    /**
     * 해당 일자(00:00~23:59:59) 주문 조회
     */
    @Transactional(readOnly = true)
    public Page<OrderDetailResponse> getDailyOrders(
            Long memberId,
            LocalDate date,
            Integer minAmount,
            Integer maxAmount,
            Pageable pageable
    ) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.plusDays(1).atStartOfDay().minusNanos(1);


        Page<Order> page = orderRepo.findByMerchantAndCreatedAtBetweenAndAmountRange(
                memberId, start, end, minAmount, maxAmount, pageable
        );

        return page.map(this::toDto);
    }


    /**
     * 해당 연·월(1일 00:00 ~ 말일 23:59:59) 주문 조회
     */
    @Transactional(readOnly = true)
    public Page<OrderDetailResponse> getMonthlyOrders(
            Long memberId,
            int year,
            int month,
            Integer minAmount,
            Integer maxAmount,
            Pageable pageable
    ) {
        LocalDate firstDay = LocalDate.of(year, month, 1);
        LocalDateTime start = firstDay.atStartOfDay();
        LocalDateTime end = firstDay.plusMonths(1).atStartOfDay().minusNanos(1);

        return orderRepo
                .findByMerchantAndCreatedAtBetweenAndAmountRange(memberId, start, end, minAmount, maxAmount, pageable)
                .map(this::toDto);
    }

    private OrderDetailResponse toDto(Order order) {


        var menus = order.getOrderMenus().stream().map(om -> {
            // 옵션 수 확인
            var opts = om.getOrderMenuOptions().stream()
                    .map(o -> {
                        return OrderDetailResponse.OrderMenuOptionResponse.builder()
                                .orderMenuOptionId(o.getOrderMenuOptionId())
                                .menuOptionId(o.getMenuOption().getMenuOptionId())
                                .optionName(o.getMenuOption().getOptionName())
                                .optionPrice(o.getOptionPrice())
                                .build();
                    })
                    .collect(Collectors.toList());

            return OrderDetailResponse.OrderMenuResponse.builder()
                    .orderMenuId(om.getOrderMenuId())
                    .menuId(om.getMenu().getMenuId())
                    .menuName(om.getMenu().getName())
                    .quantity(om.getQuantity())
                    .unitPrice(om.getUnitPrice())
                    .totalPrice(om.getTotalPrice())
                    .status(om.getStatus())
                    .options(opts)
                    .build();
        }).collect(Collectors.toList());

        return OrderDetailResponse.builder()
                .orderCode(order.getOrderCode())
                .tableNumber(order.getDiningTable().getTableNumber())
                .totalAmount(order.getTotalAmount())
                .orderStatus(order.getOrderStatus())
                .paymentStatus(order.getPaymentStatus())
                .paymentMethod(order.getPaymentMethod())
                .paidAt(order.getPaidAt())
                .createdAt(order.getCreatedAt())
                .menus(menus)
                .build();
    }

    /**
     * 주문-메뉴 상태 변경 (점주용)
     */
    @Transactional
    public void changeOrderMenuStatus(Long memberId, Long orderMenuId, OrderMenuStatus newStatus) {
        OrderMenu om = orderMenuRepo.findById(orderMenuId)
                .orElseThrow(() -> new MerchantException(MerchantErrorCode.ORDER_MENU_NOT_FOUND));

        // 이 주문메뉴가 속한 가맹점이 현재 로그인한 점주의 매장인지 확인
        Merchant merchant = om.getOrder()
                .getDiningTable()
                .getMerchant();
        if (!merchant.getMember().getMemberId().equals(memberId)) {
            throw new MerchantException(MerchantErrorCode.ACCESS_DENIED);
        }

        om.setStatus(newStatus);
    }

    @Transactional
    public OrderPrepareResponse prepareOrder(OrderPrepareRequest req) {
        // 1) 테이블 존재 확인 (상태 변경 없음)
        DiningTable table = tableRepo.findByMerchant_MerchantIdAndTableNumber(req.getMerchantId(), req.getTableId().intValue())
                .orElseThrow(() -> new MerchantException(MerchantErrorCode.TABLE_NOT_FOUND));

        // 2) 총 금액 계산
        int totalAmount = calculateTotal(req.getMenus());

        // 3) Order 생성 (ORDERED + PENDING)
        String orderCode = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyMMdd'T'HHmmss"))
                + "_t" + table.getTableNumber();

        Order order = Order.builder()
                .orderCode(orderCode)
                .orderStatus(OrderStatus.ORDERED)
                .paymentMethod(req.getPaymentMethod())
                .paymentStatus(PaymentStatus.PENDING)
                .totalAmount(totalAmount)
                .diningTable(table)
                .build();



        // 4) 메뉴·옵션 매핑
        for (OrderPrepareRequest.OrderMenu omReq : req.getMenus()) {
            var menu = menuRepo.findById(omReq.getMenuId())
                    .orElseThrow(() -> new MenuException(MenuErrorCode.MENU_NOT_FOUND));

            OrderMenu om = OrderMenu.builder()
                    .menu(menu)
                    .quantity(omReq.getQuantity())
                    .unitPrice(menu.getPrice())
                    .totalPrice(menu.getPrice() * omReq.getQuantity())
                    .build();

            if (omReq.getOptionIds() != null) {
                for (Long optId : omReq.getOptionIds()) {
                    var mo = menuOptionRepo.findById(optId)
                            .orElseThrow(() -> new MerchantException(MerchantErrorCode.OPTION_NOT_FOUND));
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

        // 5) 저장
        Order saved = orderRepo.save(order);

        // 6) 응답 반환
        return OrderPrepareResponse.builder()
                .orderCode(saved.getOrderCode())
                .merchantId(saved.getDiningTable().getMerchant().getMerchantId())
                .tableId(saved.getDiningTable().getDiningTableId())
                .createdAt(saved.getCreatedAt())
                .orderStatus(saved.getOrderStatus())
                .totalAmount(saved.getTotalAmount())
                .build();
    }

    private int calculateTotal(List<OrderPrepareRequest.OrderMenu> menus) {
        return menus.stream()
                .mapToInt(om -> {
                    int sum = menuRepo.findById(om.getMenuId())
                            .orElseThrow().getPrice() * om.getQuantity();
                    if (om.getOptionIds() != null) {
                        for (Long optId : om.getOptionIds()) {
                            sum += menuOptionRepo.findById(optId)
                                    .orElseThrow().getOptionPrice();
                        }
                    }
                    return sum;
                })
                .sum();
    }

    @Transactional(readOnly = true)
    public OrderDetailResponse getOrderByCode(String orderCode) {
        Order order = orderRepo
                .findWithDetailsByOrderCode(orderCode)
                .orElseThrow(() -> new IllegalArgumentException("Invalid orderCode: " + orderCode));
        return toDto(order);  // 기존에 정의된 변환 메서드
    }
}
