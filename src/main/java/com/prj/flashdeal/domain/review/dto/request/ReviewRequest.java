package com.prj.flashdeal.domain.review.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Schema(description = "리뷰 작성 요청")
@Getter
public class ReviewRequest {

    @Schema(description = "별점 (1~5)", example = "5")
    @NotNull(message = "평점은 필수입니다.")
    @Min(value = 1, message = "평점은 1 이상이어야 합니다.")
    @Max(value = 5, message = "평점은 5 이하이어야 합니다.")
    private Integer rating;

    @Schema(description = "리뷰 내용", example = "정말 좋은 상품이에요!")
    private String content;
}
