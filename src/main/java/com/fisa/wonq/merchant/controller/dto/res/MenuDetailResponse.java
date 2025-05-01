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

    @Schema(description = "메뉴명", example = "아메리카노")
    private String name;

    @Schema(description = "메뉴 설명", example = "부드러운 에스프레소 샷으로 만든 커피")
    private String description;

    @Schema(description = "카테고리", example = "커피")
    private String category;

    @Schema(description = "가격", example = "4500")
    private Integer price;

    @Schema(description = "이미지 URL", example = "https://.../abc.jpg")
    private String menuImgUrl;

    @Schema(description = "판매 가능 여부", example = "true")
    private Boolean isAvailable;

    @Schema(description = "옵션 그룹 목록")
    private List<OptionGroup> optionGroups;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "옵션 그룹 DTO (inner)")
    public static class OptionGroup {
        @Schema(description = "그룹 ID", example = "5")
        private Long groupId;

        @Schema(description = "그룹명", example = "사이즈")
        private String groupName;

        @Schema(description = "표시 순서", example = "0")
        private Integer displaySequence;

        @Schema(description = "기본 선택 여부", example = "true")
        private Boolean isDefault;

        @Schema(description = "이 그룹의 옵션 목록")
        private List<Option> options;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "옵션 DTO (inner)")
    public static class Option {
        @Schema(description = "옵션 ID", example = "12")
        private Long optionId;

        @Schema(description = "옵션명", example = "레귤러")
        private String optionName;

        @Schema(description = "옵션 추가 가격", example = "0")
        private Integer optionPrice;
    }
}

