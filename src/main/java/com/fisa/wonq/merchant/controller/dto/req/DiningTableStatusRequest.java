package com.fisa.wonq.merchant.controller.dto.req;

import com.fisa.wonq.merchant.domain.enums.TableStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "테이블 상태 변경 요청 DTO")
public class DiningTableStatusRequest {
    @Schema(description = "변경할 테이블 상태", example = "READY", required = true)
    private TableStatus status;
}
