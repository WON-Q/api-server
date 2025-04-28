package com.fisa.wonq.merchant.controller.dto.res;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "메뉴 등록 응답 DTO")
public class MenuResponse {
    @Schema(description = "생성된 메뉴 ID", example = "42")
    private Long menuId;
}
