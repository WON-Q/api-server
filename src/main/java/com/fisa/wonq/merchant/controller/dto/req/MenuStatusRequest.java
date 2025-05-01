package com.fisa.wonq.merchant.controller.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "메뉴 판매 상태 변경 요청 DTO")
public class MenuStatusRequest {
    @Schema(description = "판매 가능 여부", example = "false", required = true)
    private Boolean isAvailable;
}
