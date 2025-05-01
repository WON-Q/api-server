package com.fisa.wonq.order.controller.dto.req;

import com.fisa.wonq.order.domain.enums.OrderMenuStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "주문-메뉴 상태 변경 요청 DTO")
public class ChangeOrderMenuStatusRequest {
    @Schema(description = "변경할 상태", example = "SERVED", required = true)
    private OrderMenuStatus status;
}
