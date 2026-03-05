package com.prj.flashdeal.domain.deal.dto.response;

import java.time.LocalDateTime;

import com.prj.flashdeal.domain.deal.entity.Deal;
import com.prj.flashdeal.domain.deal.entity.DealStatus;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "딜 응답")
public record DealResponse(
    @Schema(description = "딜 ID", example = "1") Long dealId,
    @Schema(description = "상품 ID", example = "1") Long productId,
    @Schema(description = "상품 이름", example = "나이키 에어맥스 90") String productName,
    @Schema(description = "딜 제목", example = "나이키 에어맥스 90 한정 할인!") String title,
    @Schema(description = "원가 (원)", example = "129000") Integer originalPrice,
    @Schema(description = "할인 가격 (원)", example = "89000") Integer discountPrice,
    @Schema(description = "잔여 재고", example = "100") Integer remainingStock,
    @Schema(description = "딜 상태", example = "ACTIVE") DealStatus status,
    @Schema(description = "딜 시작 시간") LocalDateTime startAt,
    @Schema(description = "딜 종료 시간") LocalDateTime endAt
) {
    public static DealResponse from(Deal deal, int remainingStock) {
        return new DealResponse(
            deal.getId(),
            deal.getProduct().getId(),
            deal.getProduct().getName(),
            deal.getTitle(),
            deal.getProduct().getPrice(),
            deal.getDiscountPrice(),
            remainingStock,
            deal.getStatus(),
            deal.getStartAt(),
            deal.getEndAt()
        );
    }
}
