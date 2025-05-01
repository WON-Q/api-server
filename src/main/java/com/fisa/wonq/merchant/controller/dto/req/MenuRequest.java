package com.fisa.wonq.merchant.controller.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "메뉴 등록 요청 DTO")
public class MenuRequest {
    @Schema(description = "메뉴 이미지 URL (S3)", example = "https://…", required = true)
    private String menuImgUrl;

    @Schema(description = "메뉴명", example = "갈비 스테이크", required = true)
    private String name;

    @Schema(description = "카테고리", example = "스테이크", required = true)
    private String category;

    @Schema(description = "가격", example = "25000", required = true)
    private Integer price;

    @Schema(description = "판매 상태", example = "true", required = true)
    private Boolean isAvailable;

    @Schema(description = "설명", example = "부드러운 갈비살로 만든 스테이크")
    private String description;

    @Schema(description = "옵션 그룹들")
    private List<OptionGroupRequest> optionGroups;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OptionGroupRequest {
        @Schema(description = "그룹명", example = "굽기", required = true)
        private String groupName;

        @Schema(description = "옵션 리스트")
        private List<OptionRequest> options;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OptionRequest {
        @Schema(description = "옵션명", example = "레어", required = true)
        private String optionName;

        @Schema(description = "옵션 가격", example = "0", required = true)
        private Integer optionPrice;
    }
}
