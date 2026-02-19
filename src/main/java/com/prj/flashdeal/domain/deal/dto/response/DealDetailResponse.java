package com.prj.flashdeal.domain.deal.dto.response;

import java.time.LocalDateTime;

import com.prj.flashdeal.domain.deal.entity.Deal;

public record DealDetailResponse(
    Long id,
    String productName,
    String productDescription,
    Integer originalPrice,
    Integer dealPrice,
    Integer stock,
    LocalDateTime openTime,
    LocalDateTime endTime
) {
    public static DealDetailResponse from(Deal deal) {
        return new DealDetailResponse(
            deal.getId(),
            deal.getProduct().getName(),
            deal.getProduct().getDescription(),
            deal.getProduct().getPrice(),
            deal.getDealPrice(),
            deal.getStock(),
            deal.getOpenTime(),
            deal.getEndTime()
        );
    }
}
