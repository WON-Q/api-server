package com.fisa.wonq.order.service;

import com.fisa.wonq.global.websocket.service.MerchantNotificationService;
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
import com.fisa.wonq.order.controller.dto.res.OrderVerifyResponse;
import com.fisa.wonq.order.domain.Order;
import com.fisa.wonq.order.domain.OrderMenu;
import com.fisa.wonq.order.domain.OrderMenuOption;
import com.fisa.wonq.order.domain.enums.OrderMenuStatus;
import com.fisa.wonq.order.domain.enums.OrderStatus;
import com.fisa.wonq.order.domain.enums.PaymentStatus;
import com.fisa.wonq.order.exception.OrderNotFoundException;
import com.fisa.wonq.order.feign.pg.PgFeignClient;
import com.fisa.wonq.order.feign.pg.dto.BaseResponse;
import com.fisa.wonq.order.feign.pg.dto.PaymentDto;
import com.fisa.wonq.order.repository.OrderMenuRepository;
import com.fisa.wonq.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import static com.fisa.wonq.order.feign.pg.dto.PaymentStatus.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final DiningTableRepository tableRepo;
    private final MenuRepository menuRepo;
    private final MenuOptionRepository menuOptionRepo;
    private final OrderRepository orderRepo;
    private final OrderMenuRepository orderMenuRepo;
    private final PgFeignClient pgFeignClient;
    private final MerchantNotificationService notificationService;

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

        // 3. 결제 정보 준비 (다이어그램 Step 3)
        // 다이어그램에 따르면 PG사 결제는 원큐 오더 웹에서 처리됨
        // PaymentResult나 paymentService는 제거하고 Order 객체만 생성
        // 이 시점에서는 결제가 완료되지 않았으므로 ORDERED 상태와 PENDING 상태로 설정


        // 4. Order 엔티티 생성 (결제 완료 정보 반영)
        Order order = Order.builder()
                .orderCode(orderCode)
                .totalAmount(req.getTotalAmount())
                .orderStatus(OrderStatus.PAID)
                .paymentStatus(PaymentStatus.COMPLETED)
                .paymentMethod(req.getPaymentMethod())
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
                .tableId(req.getTableId())
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

    /**
     * 주문을 검증하는 메서드
     * <p>
     * PG사 API를 호출하여 결제 상태를 검증하고, 주문 상태를 업데이트합니다.
     */
    @Transactional
    public OrderVerifyResponse verifyOrder(String orderCode) {
        Order order = orderRepo.findByOrderCode(orderCode)
                .orElseThrow(() -> new OrderNotFoundException(orderCode));

        ResponseEntity<BaseResponse<PaymentDto>> response = pgFeignClient.getPaymentByOrderCode(orderCode);

        if (response.getStatusCode() != HttpStatus.OK) {
            throw new IllegalStateException("Failed to verify payment with PG: " + response.getStatusCode());
        }

        if (response.getBody() == null || response.getBody().getData() == null) {
            throw new IllegalStateException("Invalid response from PG: " + response.getBody());
        }

        PaymentDto data = response.getBody().getData();

        String message;
        switch (data.getPaymentStatus()) {
            case SUCCEEDED:
                order.updateOrderStatus(OrderStatus.PAID);
                order.updatePaymentStatus(PaymentStatus.COMPLETED);
                message = "성공적으로 결제되었습니다";
                break;
            case CANCELLED:
                order.updateOrderStatus(OrderStatus.CANCELED);
                order.updatePaymentStatus(PaymentStatus.CANCELED);
                message = "결제가 취소되었습니다";
                break;
            case FAILED:
            case EXPIRED:
                order.updateOrderStatus(OrderStatus.CANCELED);
                order.updatePaymentStatus(PaymentStatus.FAILED);
                message = "결제가 실패했습니다";
                break;
            default:
                throw new IllegalStateException("Unexpected payment status: " + data.getPaymentStatus());

        }

        Order savedOrder = orderRepo.save(order);
        
        // 결제 성공 시에만 주문 알림 전송
        if(order.getOrderStatus() == OrderStatus.PAID) {
//            notificationService.sendNewOrderNotification(savedOrder);
        }

        return OrderVerifyResponse.builder()
                .orderCode(order.getOrderCode())
                .orderStatus(order.getOrderStatus())
                .paymentStatus(order.getPaymentStatus())
                .verifiedAt(LocalDateTime.now())
                .message(message)
                .build();
    }

}
