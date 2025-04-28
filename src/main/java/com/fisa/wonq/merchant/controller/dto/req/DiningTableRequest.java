package com.fisa.wonq.merchant.controller.dto.req;

import com.fisa.wonq.merchant.domain.enums.TableStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "식탁 추가 요청 DTO")
public class DiningTableRequest {

    @Schema(description = "테이블 번호", example = "5", required = true)
    private Integer tableNumber;

    @Schema(description = "좌석 수", example = "4", required = true)
    private Integer capacity;

    @Schema(description = "상태 (READY, IN_PROGRESS)", example = "READY", required = true)
    private TableStatus status;

    @Schema(description = "위치 X 좌표", example = "10")
    private Integer locationX;

    @Schema(description = "위치 Y 좌표", example = "20")
    private Integer locationY;

    @Schema(description = "너비", example = "100")
    private Integer locationW;

    @Schema(description = "높이", example = "50")
    private Integer locationH;
}
