package com.prj.flashdeal.domain.stock.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.prj.flashdeal.domain.stock.entity.Stock;

public interface StockRepository extends JpaRepository<Stock, Long>, StockRepositoryCustom {
}
