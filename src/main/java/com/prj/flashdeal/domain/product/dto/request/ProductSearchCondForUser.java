package com.prj.flashdeal.domain.product.dto.request;

import com.prj.flashdeal.domain.product.entity.ProductCategory;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductSearchCondForUser {

    private String productName;

    private Integer minPrice;
    private Integer maxPrice;

    private ProductCategory category;
}
