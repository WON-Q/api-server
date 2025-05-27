//package com.fisa.wonq.order.feign.pg;
//
//import com.fisa.wonq.order.feign.pg.dto.BaseResponse;
//import com.fisa.wonq.order.feign.pg.dto.PaymentStatus;
//import com.fisa.wonq.order.feign.pg.dto.PaymentVerifyRequestDto;
//import com.fisa.wonq.order.feign.pg.dto.PaymentVerifyResponseDto;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
//import org.springframework.http.ResponseEntity;
//import org.springframework.test.context.ActiveProfiles;
//
//import static com.github.tomakehurst.wiremock.client.WireMock.*;
//import static org.assertj.core.api.Assertions.assertThat;
//
//@SpringBootTest
//@ActiveProfiles("test")
//@AutoConfigureWireMock(port = 0) // WireMock 서버를 자동으로 설정
//public class PgFeignClientTest {
//
//    @Autowired
//    private PgFeignClient pgFeignClient;
//
//    @Test
//    void verifyPayment_shouldReturnSuccessResponse() {
//        // Given
//        String transactionId = "tx_123456789";
//        String successJson = """
//                {
//                    "isSuccess": true,
//                    "message": "Payment verified",
//                    "data": {
//                        "transactionId": "tx_123456789",
//                        "status": "SUCCEEDED"
//                    }
//                }
//                """;
//
//        // Mock PG API
//        stubFor(post(urlEqualTo("/payments/verify"))
//                .withRequestBody(equalToJson("{\"transactionId\":\"" + transactionId + "\"}"))
//                .willReturn(aResponse()
//                        .withStatus(200)
//                        .withHeader("Content-Type", "application/json")
//                        .withBody(successJson)));
//
//        // When
//        PaymentVerifyRequestDto requestDto = new PaymentVerifyRequestDto(transactionId);
//        ResponseEntity<BaseResponse<PaymentVerifyResponseDto>> response = pgFeignClient.verifyPayment(requestDto);
//
//        // Then
//        assertThat(response.getStatusCode().value()).isEqualTo(200);
//        assertThat(response.getBody().getIsSuccess()).isTrue();
//        assertThat(response.getBody().getData().getTransactionId()).isEqualTo(transactionId);
//        assertThat(response.getBody().getData().getStatus()).isEqualTo(PaymentStatus.SUCCEEDED);
//    }
//
//    @Test
//    void verifyPayment_shouldReturnFailedResponse() {
//        // Given
//        String transactionId = "tx_failed_123";
//        String failedJson = """
//                {
//                    "isSuccess": true,
//                    "message": "Payment verification completed",
//                    "data": {
//                        "transactionId": "tx_failed_123",
//                        "status": "FAILED"
//                    }
//                }
//                """;
//
//        // Mock PG API
//        stubFor(post(urlEqualTo("/payments/verify"))
//                .withRequestBody(equalToJson("{\"transactionId\":\"" + transactionId + "\"}"))
//                .willReturn(aResponse()
//                        .withStatus(200)
//                        .withHeader("Content-Type", "application/json")
//                        .withBody(failedJson)));
//
//        // When
//        PaymentVerifyRequestDto requestDto = new PaymentVerifyRequestDto(transactionId);
//        ResponseEntity<BaseResponse<PaymentVerifyResponseDto>> response = pgFeignClient.verifyPayment(requestDto);
//
//        // Then
//        assertThat(response.getStatusCode().value()).isEqualTo(200);
//        assertThat(response.getBody().getIsSuccess()).isTrue();
//        assertThat(response.getBody().getData().getTransactionId()).isEqualTo(transactionId);
//        assertThat(response.getBody().getData().getStatus()).isEqualTo(PaymentStatus.FAILED);
//    }
//}