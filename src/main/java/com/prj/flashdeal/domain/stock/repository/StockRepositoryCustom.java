package com.prj.flashdeal.domain.stock.repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.prj.flashdeal.domain.stock.entity.Stock;

public interface StockRepositoryCustom {
    Optional<Stock> findByProductId(Long productId);
    Optional<Stock> findByProductIdWithLock(Long productId);
    Map<Long, Integer> findQuantitiesByProductIds(List<Long> productIds);
    long decreaseQuantity(Long productId, int quantity);
    long increaseQuantity(Long productId, int quantity);
}
