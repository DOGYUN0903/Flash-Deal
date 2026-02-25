package com.prj.flashdeal.domain.product.dto.request;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import com.prj.flashdeal.domain.product.entity.ProductCategory;
import com.prj.flashdeal.domain.product.entity.ProductStatus;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductSearchCondForAdmin {

    private String productName;

    private Integer minPrice;
    private Integer maxPrice;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    private ProductStatus status;

    private Boolean isDeleted;

    private ProductCategory category;
}
