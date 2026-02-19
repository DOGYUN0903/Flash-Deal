package com.prj.flashdeal.domain.deal.dto.response;

import java.time.LocalDateTime;

import com.prj.flashdeal.domain.deal.entity.Deal;

public record DealSummaryResponse(
    Long id,
    String productName,
    Integer dealPrice,
    Integer stock,
    LocalDateTime openTime,
    LocalDateTime endTime
) {
    public static DealSummaryResponse from(Deal deal) {
        return new DealSummaryResponse(
            deal.getId(),
            deal.getProduct().getName(),
            deal.getDealPrice(),
            deal.getStock(),
            deal.getOpenTime(),
            deal.getEndTime()
        );
    }
}
