package com.prj.flashdeal.domain.review.dto.response;

import java.time.LocalDateTime;

import com.prj.flashdeal.domain.review.entity.Review;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "리뷰 응답")
public record ReviewResponse(
    @Schema(description = "리뷰 ID", example = "1") Long reviewId,
    @Schema(description = "작성자 이름", example = "홍길동") String memberName,
    @Schema(description = "별점 (1~5)", example = "5") Integer rating,
    @Schema(description = "리뷰 내용", example = "정말 좋은 상품이에요!") String content,
    @Schema(description = "작성일시", example = "2024-01-01T12:00:00") LocalDateTime createdAt
) {
    public static ReviewResponse from(Review review) {
        return new ReviewResponse(
            review.getId(),
            review.getMember().getName(),
            review.getRating(),
            review.getContent(),
            review.getCreatedAt()
        );
    }
}
