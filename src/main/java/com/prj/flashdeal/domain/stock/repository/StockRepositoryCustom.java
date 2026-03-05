package com.prj.flashdeal.domain.stock.repository;

import java.util.Optional;

import com.prj.flashdeal.domain.stock.entity.Stock;

public interface StockRepositoryCustom {
    Optional<Stock> findByProductId(Long productId);
    Optional<Stock> findByProductIdWithLock(Long productId);
}
