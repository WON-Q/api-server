package com.fisa.wonq.merchant.controller.dto.res;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "메뉴 판매 상태 변경 응답 DTO")
public class MenuStatusResponse {
    @Schema(description = "메뉴 ID", example = "42")
    private Long menuId;

    @Schema(description = "변경된 판매 가능 여부", example = "false")
    private Boolean isAvailable;
}
