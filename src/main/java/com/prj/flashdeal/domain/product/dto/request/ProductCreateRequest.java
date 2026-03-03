package com.prj.flashdeal.domain.product.dto.request;

import com.prj.flashdeal.domain.product.entity.ProductCategory;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;

import org.springframework.web.multipart.MultipartFile;

@Schema(description = "상품 등록 요청")
@Getter
@NoArgsConstructor
public class ProductCreateRequest {

    @Schema(description = "상품 이름", example = "나이키 에어맥스 90")
    @NotBlank(message = "상품 명은 필수 입력 값입니다.")
    private String name;

    @Schema(description = "상품 설명", example = "클래식한 디자인의 나이키 에어맥스 90 운동화입니다.")
    @NotBlank(message = "상품 설명은 필수 입력 값입니다.")
    private String description;

    @Schema(description = "상품 가격 (원)", example = "129000")
    @NotNull(message = "상품 가격은 필수 입력 값입니다.")
    @Positive(message = "상품 가격은 0보다 커야 합니다.")
    private Integer price;

    @Schema(description = "재고 수량", example = "100")
    @NotNull(message = "재고는 필수 입력 값입니다.")
    @Min(value = 0, message = "재고는 0 이상이어야 합니다.")
    private Integer stock;

    @Schema(description = "상품 카테고리", example = "FASHION")
    @NotNull(message = "카테고리는 필수 입력 값입니다.")
    private ProductCategory category;

    @Schema(description = "상품 이미지 파일")
    private MultipartFile image;
}
