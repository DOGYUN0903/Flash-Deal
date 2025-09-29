package com.prj.flashdeal.domain.product.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProductStatus {

    PREPARING("판매 준비중"),
    ON_SALE("판매중"),
    SOLD_OUT("품절");

    private final String description;
}
