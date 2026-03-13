package com.prj.flashdeal.domain.deal.dto.cache;

import java.util.List;
import java.util.Map;

import com.prj.flashdeal.domain.deal.dto.response.DealResponse;
import com.prj.flashdeal.global.response.PageResponse;

public record DealListPageCacheValue(
    List<DealListItemCacheValue> content,
    int pageNumber,
    int pageSize,
    long totalElements,
    int totalPages
) {
    public PageResponse<DealResponse> toResponse(Map<Long, Integer> stockByProductId) {
        List<DealResponse> responses = content.stream()
            .map(item -> item.toResponse(stockByProductId.getOrDefault(item.productId(), 0)))
            .toList();

        return new PageResponse<>(responses, pageNumber, pageSize, totalElements, totalPages);
    }
}
