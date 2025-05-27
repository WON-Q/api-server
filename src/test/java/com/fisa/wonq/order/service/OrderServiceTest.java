//package com.fisa.wonq.order.service;
//
//import com.fisa.wonq.order.controller.dto.res.OrderVerifyResponse;
//import com.fisa.wonq.order.domain.Order;
//import com.fisa.wonq.order.domain.enums.OrderStatus;
//import com.fisa.wonq.order.domain.enums.PaymentStatus;
//import com.fisa.wonq.order.feign.pg.PgFeignClient;
//import com.fisa.wonq.order.feign.pg.dto.BaseResponse;
//import com.fisa.wonq.order.feign.pg.dto.PaymentVerifyResponseDto;
//import com.fisa.wonq.order.repository.OrderRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//
//import java.util.Optional;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//public class OrderServiceTest {
//
//    @Mock
//    private OrderRepository orderRepo;
//
//    @Mock
//    private PgFeignClient pgFeignClient;
//
//    @InjectMocks
//    private OrderService orderService;
//
//    private Order pendingOrder;
//    private final String orderCode = "220101T1200_t1";
//    private final String transactionId = "tx_123456789";
//
//    @BeforeEach
//    void setUp() {
//        pendingOrder = mock(Order.class);
//        when(pendingOrder.getOrderCode()).thenReturn(orderCode);
//        when(pendingOrder.getPaymentStatus()).thenReturn(PaymentStatus.PENDING);
//    }
//
//    @Test
//    void verifyOrder_withSucceededPayment_shouldUpdateStatusToCompleted() {
//        // Given
//        BaseResponse<PaymentVerifyResponseDto> responseBody = new BaseResponse<>();
//        responseBody.setIsSuccess(true);
//
//        PaymentVerifyResponseDto paymentResponse = new PaymentVerifyResponseDto();
//        paymentResponse.setTransactionId(transactionId);
//        paymentResponse.setStatus(com.fisa.wonq.order.feign.pg.dto.PaymentStatus.SUCCEEDED);
//        responseBody.setData(paymentResponse);
//
//        when(orderRepo.findByOrderCode(orderCode)).thenReturn(Optional.of(pendingOrder));
//        when(pgFeignClient.verifyPayment(any())).thenReturn(
//                new ResponseEntity<>(responseBody, HttpStatus.OK)
//        );
//
//        when(pendingOrder.getOrderStatus()).thenReturn(OrderStatus.PAID);
//        when(pendingOrder.getPaymentStatus()).thenReturn(PaymentStatus.SUCCEEDED);
//
//        // When
//        OrderVerifyResponse response = orderService.verifyOrder(orderCode, transactionId);
//
//        // Then
//        verify(pendingOrder).updateOrderStatus(OrderStatus.PAID);
//        verify(pendingOrder).updatePaymentStatus(PaymentStatus.SUCCEEDED);
//        assertThat(response.getOrderCode()).isEqualTo(orderCode);
//        assertThat(response.getOrderStatus()).isEqualTo(OrderStatus.PAID);
//        assertThat(response.getPaymentStatus()).isEqualTo(PaymentStatus.SUCCEEDED);
//        assertThat(response.getMessage()).contains("successfully");
//    }
//
//    @Test
//    void verifyOrder_withCanceledPayment_shouldUpdateStatusToCanceled() {
//        // Given
//        BaseResponse<PaymentVerifyResponseDto> responseBody = new BaseResponse<>();
//        responseBody.setIsSuccess(true);
//
//        PaymentVerifyResponseDto paymentResponse = new PaymentVerifyResponseDto();
//        paymentResponse.setTransactionId(transactionId);
//        paymentResponse.setStatus(com.fisa.wonq.order.feign.pg.dto.PaymentStatus.CANCELED);
//        responseBody.setData(paymentResponse);
//
//        when(orderRepo.findByOrderCode(orderCode)).thenReturn(Optional.of(pendingOrder));
//        when(pgFeignClient.verifyPayment(any())).thenReturn(
//                new ResponseEntity<>(responseBody, HttpStatus.OK)
//        );
//
//        when(pendingOrder.getOrderStatus()).thenReturn(OrderStatus.CANCELED);
//        when(pendingOrder.getPaymentStatus()).thenReturn(PaymentStatus.CANCELLED);
//
//        // When
//        OrderVerifyResponse response = orderService.verifyOrder(orderCode, transactionId);
//
//        // Then
//        verify(pendingOrder).updateOrderStatus(OrderStatus.CANCELED);
//        verify(pendingOrder).updatePaymentStatus(PaymentStatus.CANCELLED);
//        assertThat(response.getOrderStatus()).isEqualTo(OrderStatus.CANCELED);
//        assertThat(response.getPaymentStatus()).isEqualTo(PaymentStatus.CANCELLED);
//        assertThat(response.getMessage()).contains("canceled");
//    }
//
//    @Test
//    void verifyOrder_withFailedPayment_shouldUpdateStatusToFailed() {
//        // Given
//        BaseResponse<PaymentVerifyResponseDto> responseBody = new BaseResponse<>();
//        responseBody.setIsSuccess(true);
//
//        PaymentVerifyResponseDto paymentResponse = new PaymentVerifyResponseDto();
//        paymentResponse.setTransactionId(transactionId);
//        paymentResponse.setStatus(com.fisa.wonq.order.feign.pg.dto.PaymentStatus.FAILED);
//        responseBody.setData(paymentResponse);
//
//        when(orderRepo.findByOrderCode(orderCode)).thenReturn(Optional.of(pendingOrder));
//        when(pgFeignClient.verifyPayment(any())).thenReturn(
//                new ResponseEntity<>(responseBody, HttpStatus.OK)
//        );
//
//        when(pendingOrder.getOrderStatus()).thenReturn(OrderStatus.CANCELED);
//        when(pendingOrder.getPaymentStatus()).thenReturn(PaymentStatus.FAILED);
//
//        // When
//        OrderVerifyResponse response = orderService.verifyOrder(orderCode, transactionId);
//
//        // Then
//        verify(pendingOrder).updateOrderStatus(OrderStatus.CANCELED);
//        verify(pendingOrder).updatePaymentStatus(PaymentStatus.FAILED);
//        assertThat(response.getOrderStatus()).isEqualTo(OrderStatus.CANCELED);
//        assertThat(response.getPaymentStatus()).isEqualTo(PaymentStatus.FAILED);
//        assertThat(response.getMessage()).contains("failed");
//    }
//
//    @Test
//    void verifyOrder_withApiError_shouldThrowException() {
//        // Given
//        BaseResponse<PaymentVerifyResponseDto> responseBody = new BaseResponse<>();
//        responseBody.setIsSuccess(false);
//        responseBody.setMessage("API Error");
//
//        when(orderRepo.findByOrderCode(orderCode)).thenReturn(Optional.of(pendingOrder));
//        when(pgFeignClient.verifyPayment(any())).thenReturn(
//                new ResponseEntity<>(responseBody, HttpStatus.OK)
//        );
//
//        // When, Then
//        IllegalStateException exception = assertThrows(
//                IllegalStateException.class,
//                () -> orderService.verifyOrder(orderCode, transactionId)
//        );
//        assertThat(exception.getMessage()).contains("verification failed");
//    }
//
//    @Test
//    void verifyOrder_withNonPendingOrder_shouldThrowException() {
//        // Given
//        Order completedOrder = mock(Order.class);
//        when(completedOrder.getPaymentStatus()).thenReturn(PaymentStatus.SUCCEEDED);
//        when(orderRepo.findByOrderCode(orderCode)).thenReturn(Optional.of(completedOrder));
//
//        // When, Then
//        IllegalStateException exception = assertThrows(
//                IllegalStateException.class,
//                () -> orderService.verifyOrder(orderCode, transactionId)
//        );
//        assertThat(exception.getMessage()).contains("Invalid order status");
//    }
//
//    @Test
//    void verifyOrder_withInvalidOrderCode_shouldThrowException() {
//        // Given
//        when(orderRepo.findByOrderCode(orderCode)).thenReturn(Optional.empty());
//
//        // When, Then
//        IllegalArgumentException exception = assertThrows(
//                IllegalArgumentException.class,
//                () -> orderService.verifyOrder(orderCode, transactionId)
//        );
//        assertThat(exception.getMessage()).contains("Order not found");
//    }
//}