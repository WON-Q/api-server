package com.fisa.wonq.merchant.controller.dto.res;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "테이블 정보 수정 응답 DTO")
public class DiningTableUpdateResponse {
    @Schema(description = "테이블 ID", example = "10")
    private Long diningTableId;

    @Schema(description = "변경된 테이블 번호", example = "7")
    private Integer tableNumber;

    @Schema(description = "변경된 좌석 수", example = "6")
    private Integer capacity;
}
