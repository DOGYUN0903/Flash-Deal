package com.prj.flashdeal.domain.stock.repository.impl;

import static com.prj.flashdeal.domain.stock.entity.QStock.*;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.prj.flashdeal.domain.stock.entity.Stock;
import com.prj.flashdeal.domain.stock.repository.StockRepositoryCustom;
import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.LockModeType;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class StockRepositoryCustomImpl implements StockRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<Stock> findByProductId(Long productId) {
        return Optional.ofNullable(
            queryFactory
                .selectFrom(stock)
                .where(stock.product.id.eq(productId))
                .fetchOne()
        );
    }

    @Override
    public Optional<Stock> findByProductIdWithLock(Long productId) {
        return Optional.ofNullable(
            queryFactory
                .selectFrom(stock)
                .where(stock.product.id.eq(productId))
                .setLockMode(LockModeType.PESSIMISTIC_WRITE)
                .fetchOne()
        );
    }
}
