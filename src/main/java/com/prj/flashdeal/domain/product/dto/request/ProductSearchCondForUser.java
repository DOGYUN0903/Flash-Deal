package com.prj.flashdeal.domain.product.dto.request;

import com.prj.flashdeal.domain.product.entity.ProductCategory;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "사용자 상품 검색 조건")
@Getter
@Setter
public class ProductSearchCondForUser {

    @Schema(description = "상품명 (부분 일치)", example = "나이키")
    private String productName;

    @Schema(description = "최소 가격", example = "10000")
    private Integer minPrice;

    @Schema(description = "최대 가격", example = "100000")
    private Integer maxPrice;

    @Schema(description = "카테고리", example = "ELECTRONICS")
    private ProductCategory category;

    @Schema(description = "품절 상품 제외 여부", example = "true")
    private boolean excludeSoldOut;
}
