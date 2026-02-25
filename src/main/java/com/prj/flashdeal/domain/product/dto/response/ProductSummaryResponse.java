package com.prj.flashdeal.domain.product.dto.response;

import com.prj.flashdeal.domain.product.entity.ProductCategory;
import com.prj.flashdeal.domain.product.entity.ProductStatus;

public record ProductSummaryResponse(
    Long productId,
    String name,
    Integer price,
    Integer stockQuantity,
    ProductStatus status,
    String imageUrl,
    ProductCategory category,
    Long reviewCount,
    Double averageRating
) {}
