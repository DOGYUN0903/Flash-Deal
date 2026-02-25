package com.prj.flashdeal.domain.product.dto.response;

import com.prj.flashdeal.domain.product.entity.Product;
import com.prj.flashdeal.domain.product.entity.ProductCategory;
import com.prj.flashdeal.domain.product.entity.ProductStatus;

public record ProductResponse(
    Long productId,
    String name,
    String description,
    Integer price,
    Integer stockQuantity,
    ProductStatus status,
    String imageUrl,
    ProductCategory category
) {
    public static ProductResponse from(Product product) {
        return new ProductResponse(
            product.getId(),
            product.getName(),
            product.getDescription(),
            product.getPrice(),
            product.getStockQuantity(),
            product.getStatus(),
            product.getImageUrl(),
            product.getCategory()
        );
    }
}
