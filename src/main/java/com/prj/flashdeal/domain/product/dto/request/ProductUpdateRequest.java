package com.prj.flashdeal.domain.product.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import org.springframework.web.multipart.MultipartFile;

@Getter
@NoArgsConstructor
public class ProductUpdateRequest {

    private String name;
    private String description;
    private Integer price;
    private Integer stock;
    private MultipartFile image;
}
