package com.fisa.wonq.merchant.controller.dto.res;

import com.fisa.wonq.merchant.domain.enums.TableStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "테이블 상태 변경 응답 DTO")
public class DiningTableStatusResponse {
    @Schema(description = "테이블 ID", example = "10")
    private Long diningTableId;

    @Schema(description = "변경된 테이블 상태", example = "READY")
    private TableStatus status;
}
