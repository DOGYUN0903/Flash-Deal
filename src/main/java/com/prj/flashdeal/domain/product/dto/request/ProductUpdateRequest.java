package com.prj.flashdeal.domain.product.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import org.springframework.web.multipart.MultipartFile;

@Schema(description = "상품 수정 요청")
@Getter
@NoArgsConstructor
public class ProductUpdateRequest {

    @Schema(description = "상품 이름 (변경할 경우에만 입력)", example = "나이키 에어맥스 270")
    private String name;

    @Schema(description = "상품 설명 (변경할 경우에만 입력)", example = "업데이트된 상품 설명입니다.")
    private String description;

    @Schema(description = "상품 가격 (원, 변경할 경우에만 입력)", example = "149000")
    private Integer price;

    @Schema(description = "재고 수량 (변경할 경우에만 입력)", example = "200")
    private Integer stock;

    @Schema(description = "상품 이미지 파일 (교체할 경우에만 업로드)")
    private MultipartFile image;
}
