package com.prj.flashdeal.domain.stock.repository.impl;

import static com.prj.flashdeal.domain.stock.entity.QStock.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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

    @Override
    public Map<Long, Integer> findQuantitiesByProductIds(List<Long> productIds) {
        if (productIds == null || productIds.isEmpty()) {
            return Collections.emptyMap();
        }

        return queryFactory
            .select(stock.product.id, stock.quantity)
            .from(stock)
            .where(stock.product.id.in(productIds))
            .fetch()
            .stream()
            .collect(Collectors.toMap(
                tuple -> tuple.get(stock.product.id),
                tuple -> tuple.get(stock.quantity)
            ));
    }

    @Override
    public long decreaseQuantity(Long productId, int quantity) {
        return queryFactory
            .update(stock)
            .set(stock.quantity, stock.quantity.subtract(quantity))
            .where(
                stock.product.id.eq(productId),
                stock.quantity.goe(quantity)
            )
            .execute();
    }

    @Override
    public long increaseQuantity(Long productId, int quantity) {
        return queryFactory
            .update(stock)
            .set(stock.quantity, stock.quantity.add(quantity))
            .where(stock.product.id.eq(productId))
            .execute();
    }
}
