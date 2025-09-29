package com.prj.flashdeal.domain.product.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
}
