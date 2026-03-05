package com.prj.flashdeal.domain.product.dto.request;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import com.prj.flashdeal.domain.product.entity.ProductCategory;
import com.prj.flashdeal.domain.product.entity.ProductStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "어드민 상품 검색 조건")
@Getter
@Setter
public class ProductSearchCondForAdmin {

    @Schema(description = "상품명 (부분 일치)", example = "나이키")
    private String productName;

    @Schema(description = "최소 가격", example = "10000")
    private Integer minPrice;

    @Schema(description = "최대 가격", example = "100000")
    private Integer maxPrice;

    @Schema(description = "등록일 시작 (yyyy-MM-dd)", example = "2026-01-01")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @Schema(description = "등록일 종료 (yyyy-MM-dd)", example = "2026-12-31")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    @Schema(description = "상품 상태", example = "ON_SALE")
    private ProductStatus status;

    @Schema(description = "삭제 여부", example = "false")
    private Boolean isDeleted;

    @Schema(description = "카테고리", example = "ELECTRONICS")
    private ProductCategory category;
}
