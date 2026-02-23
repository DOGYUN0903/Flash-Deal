package com.prj.flashdeal.domain.product.dto.response;

import com.prj.flashdeal.domain.product.entity.Product;
import com.prj.flashdeal.domain.product.entity.ProductStatus;

public record ProductResponseForUser(
    Long productId,
    String name,
    String description,
    Integer price,
    ProductStatus status,
    String imageUrl
) {
    public static ProductResponseForUser from(Product product) {
        return new ProductResponseForUser(
            product.getId(),
            product.getName(),
            product.getDescription(),
            product.getPrice(),
            product.getStatus(),
            product.getImageUrl()
        );
    }
}
