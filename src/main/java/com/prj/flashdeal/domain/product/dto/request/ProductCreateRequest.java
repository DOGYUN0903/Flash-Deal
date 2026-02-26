package com.prj.flashdeal.domain.product.dto.request;

import com.prj.flashdeal.domain.product.entity.ProductCategory;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;

import org.springframework.web.multipart.MultipartFile;

@Getter
@NoArgsConstructor
public class ProductCreateRequest {

    @NotBlank(message = "상품 명은 필수 입력 값입니다.")
    private String name;

    @NotBlank(message = "상품 설명은 필수 입력 값입니다.")
    private String description;

    @NotNull(message = "상품 가격은 필수 입력 값입니다.")
    @Positive(message = "상품 가격은 0보다 커야 합니다.")
    private Integer price;

    @NotNull(message = "재고는 필수 입력 값입니다.")
    @Min(value = 0, message = "재고는 0 이상이어야 합니다.")
    private Integer stock;

    @NotNull(message = "카테고리는 필수 입력 값입니다.")
    private ProductCategory category;

    private MultipartFile image;
}
