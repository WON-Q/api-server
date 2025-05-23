package com.fisa.wonq.member.controller.dto.res;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "사업자등록증 OCR 결과")
public class OcrResponseDTO {

    @Schema(description = "사업자등록번호", example = "123-45-67890")
    private String businessRegistrationNo;

//    @Schema(description = "상호명", example = "원큐식당")
//    private String merchantName;
//
//    @Schema(description = "대표자명", example = "홍길동")
//    private String representativeName;
//
}
