package com.fisa.wonq.merchant.controller.dto.req;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "테이블 정보 수정 요청 DTO")
public class DiningTableUpdateRequest {
    @Schema(description = "테이블 번호", example = "7")
    private Integer tableNumber;

    @Schema(description = "좌석 수", example = "6")
    private Integer capacity;
}
