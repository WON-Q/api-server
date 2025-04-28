// src/main/java/com/fisa/wonq/dining/controller/dto/DiningTableResponse.java
package com.fisa.wonq.merchant.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "식탁 추가 응답 DTO")
public class DiningTableResponse {
    @Schema(description = "생성된 식탁 ID", example = "10")
    private Long diningTableId;
}
