package com.prj.flashdeal.domain.product.dto.response;

import com.prj.flashdeal.domain.product.entity.ProductCategory;
import com.prj.flashdeal.domain.product.entity.ProductStatus;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "상품 목록 요약 응답")
public record ProductSummaryResponse(
    @Schema(description = "상품 ID", example = "1") Long productId,
    @Schema(description = "상품 이름", example = "나이키 에어맥스 90") String name,
    @Schema(description = "상품 가격 (원)", example = "129000") Integer price,
    @Schema(description = "재고 수량", example = "100") Integer stockQuantity,
    @Schema(description = "상품 상태", example = "ON_SALE") ProductStatus status,
    @Schema(description = "상품 이미지 URL", example = "https://cdn.example.com/products/1.jpg") String imageUrl,
    @Schema(description = "상품 카테고리", example = "FASHION") ProductCategory category,
    @Schema(description = "리뷰 수", example = "42") Long reviewCount,
    @Schema(description = "평균 평점", example = "4.5") Double averageRating
) {}
