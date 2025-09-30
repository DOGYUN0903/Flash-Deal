package com.prj.flashdeal.domain.product.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductSearchCondForUser {

    private String productName;

    private Integer minPrice;
    private Integer maxPrice;
}
