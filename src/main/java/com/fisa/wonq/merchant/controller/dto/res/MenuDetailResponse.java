package com.fisa.wonq.merchant.controller.dto.res;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "메뉴 상세 조회 응답 DTO")
public class MenuDetailResponse {
    @Schema(description = "메뉴 ID", example = "42")
    private Long menuId;

    @Schema(description = "메뉴명", example = "갈비 스테이크")
    private String name;

    @Schema(description = "설명", example = "부드러운 갈비살로 만든 스테이크")
    private String description;

    @Schema(description = "카테고리", example = "스테이크")
    private String category;

    @Schema(description = "가격", example = "25000")
    private Integer price;

    @Schema(description = "메뉴 이미지 URL", example = "https://…")
    private String menuImgUrl;

    @Schema(description = "판매 가능 여부", example = "true")
    private Boolean isAvailable;

    @Schema(description = "옵션 그룹들")
    private List<OptionGroup> optionGroups;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "옵션 그룹 DTO")
    public static class OptionGroup {
        @Schema(description = "옵션 그룹 ID", example = "7")
        private Long groupId;

        @Schema(description = "그룹명", example = "굽기")
        private String groupName;

        @Schema(description = "표시 순서", example = "0")
        private Integer displaySequence;

        @Schema(description = "기본 그룹 여부", example = "true")
        private Boolean isDefault;

        @Schema(description = "그룹에 속한 옵션들")
        private List<Option> options;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "개별 옵션 DTO")
    public static class Option {
        @Schema(description = "옵션 ID", example = "100")
        private Long optionId;

        @Schema(description = "옵션명", example = "레어")
        private String optionName;

        @Schema(description = "옵션 가격", example = "0")
        private Integer optionPrice;
    }
}

