package com.prj.flashdeal.domain.deal.dto.cache;

import java.time.LocalDateTime;

import com.prj.flashdeal.domain.deal.dto.response.DealResponse;
import com.prj.flashdeal.domain.deal.entity.DealStatus;

public record DealListItemCacheValue(
    Long dealId,
    Long productId,
    String productName,
    String title,
    Integer originalPrice,
    Integer discountPrice,
    DealStatus status,
    LocalDateTime startAt,
    LocalDateTime endAt
) {
    public DealResponse toResponse(int remainingStock) {
        return new DealResponse(
            dealId,
            productId,
            productName,
            title,
            originalPrice,
            discountPrice,
            remainingStock,
            status,
            startAt,
            endAt
        );
    }
}
