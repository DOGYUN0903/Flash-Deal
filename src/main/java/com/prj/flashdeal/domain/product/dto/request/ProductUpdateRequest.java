package com.prj.flashdeal.domain.product.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProductUpdateRequest {

    private String name;
    private String description;
    private Integer price;
}
