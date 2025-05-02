package com.fisa.wonq.member.controller.dto.res;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "로그인 이력 응답 DTO")
public class LoginHistoryResponse {
    @Schema(description = "로그인 일시")
    private LocalDateTime loginAt;

    @Schema(description = "IP 주소")
    private String ipAddress;

    @Schema(description = "User-Agent")
    private String userAgent;
}
