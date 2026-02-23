package com.prj.flashdeal.domain.product.dto.response;

import com.prj.flashdeal.domain.product.entity.ProductStatus;

public record ProductSummaryResponse(
    Long productId,
    String name,
    Integer price,
    Integer stockQuantity,
    ProductStatus status,
    Long reviewCount,
    Double averageRating
) {}
