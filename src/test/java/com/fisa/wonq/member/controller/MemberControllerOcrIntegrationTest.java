// src/test/java/com/fisa/wonq/member/controller/MemberControllerOcrIntegrationTest.java
package com.fisa.wonq.member.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * OCR 컨트롤러의 /api/v1/auth/ocr 엔드포인트가
 * OcrResponseDTO 형태로 올바르게 응답하는지 검증하는 통합 테스트
 */
@SpringBootTest
@AutoConfigureMockMvc
class MemberControllerOcrIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("POST /api/v1/auth/ocr - 응답 JSON이 OcrResponseDTO 구조를 갖는다")
    void extractBusinessInfo_returnsProperDto() throws Exception {
        // 테스트용 샘플 이미지 로드 (src/test/resources/sample-cert.png)
        var resource = new ClassPathResource("sample-cert.png");
        var file = new MockMultipartFile(
                "file",
                "sample-cert.png",
                MediaType.IMAGE_PNG_VALUE,
                resource.getInputStream()
        );

        mockMvc.perform(multipart("/api/v1/auth/ocr")
                        .file(file)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.businessRegistrationNo", not(emptyString())));
    }
}
