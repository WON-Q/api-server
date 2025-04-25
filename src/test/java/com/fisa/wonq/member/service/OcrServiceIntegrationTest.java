package com.fisa.wonq.member.service;

import com.fisa.wonq.member.controller.dto.OcrResponseDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class OcrServiceIntegrationTest {

    @Autowired
    private OcrService ocrService;

    @Test
    void extract_withSampleImage_shouldReturnNonEmptyFields() throws Exception {
        // 1) 테스트 리소스에 넣어둔 샘플 사업자등록증 이미지 로드
        ClassPathResource resource = new ClassPathResource("sample-cert.png");
        try (InputStream is = resource.getInputStream()) {
            MultipartFile file = new MockMultipartFile(
                    "file",
                    "sample-cert.png",
                    MediaType.IMAGE_PNG_VALUE,
                    is
            );

            // 2) 실제 OCR 호출
            OcrResponseDTO dto = ocrService.extract(file);

            // 3) 추출된 값 검증 (예시: 모두 비어있지 않아야 함)
            assertThat(dto.getBusinessRegistrationNo()).isNotBlank();
        }
    }
}
