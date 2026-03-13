package com.prj.flashdeal.domain.deal.repository.impl;

import static com.prj.flashdeal.domain.deal.entity.QDeal.*;
import static com.prj.flashdeal.domain.product.entity.QProduct.*;
import static com.prj.flashdeal.domain.stock.entity.QStock.*;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import com.prj.flashdeal.domain.deal.dto.cache.DealDetailCacheValue;
import com.prj.flashdeal.domain.deal.dto.cache.DealListItemCacheValue;
import com.prj.flashdeal.domain.deal.dto.response.DealResponse;
import com.prj.flashdeal.domain.deal.repository.DealRepositoryCustom;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class DealRepositoryCustomImpl implements DealRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<DealResponse> findDealsWithStock(Pageable pageable) {
        List<DealResponse> content = queryFactory
            .select(Projections.constructor(DealResponse.class,
                deal.id,
                product.id,
                product.name,
                deal.title,
                product.price,
                deal.discountPrice,
                stock.quantity.coalesce(0),
                deal.status,
                deal.startAt,
                deal.endAt))
            .from(deal)
            .join(deal.product, product)
            .leftJoin(stock).on(stock.product.id.eq(product.id))
            .orderBy(deal.createdAt.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        JPAQuery<Long> countQuery = queryFactory
            .select(deal.count())
            .from(deal);

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    @Override
    public Page<DealListItemCacheValue> findDealListCacheValues(Pageable pageable) {
        List<DealListItemCacheValue> content = queryFactory
            .select(Projections.constructor(DealListItemCacheValue.class,
                deal.id,
                product.id,
                product.name,
                deal.title,
                product.price,
                deal.discountPrice,
                deal.status,
                deal.startAt,
                deal.endAt))
            .from(deal)
            .join(deal.product, product)
            .orderBy(deal.createdAt.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        JPAQuery<Long> countQuery = queryFactory
            .select(deal.count())
            .from(deal);

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    @Override
    public DealResponse findDealWithStock(Long dealId) {
        return queryFactory
            .select(Projections.constructor(DealResponse.class,
                deal.id,
                product.id,
                product.name,
                deal.title,
                product.price,
                deal.discountPrice,
                stock.quantity.coalesce(0),
                deal.status,
                deal.startAt,
                deal.endAt))
            .from(deal)
            .join(deal.product, product)
            .leftJoin(stock).on(stock.product.id.eq(product.id))
            .where(deal.id.eq(dealId))
            .fetchOne();
    }

    @Override
    public DealDetailCacheValue findDealDetailCacheValue(Long dealId) {
        return queryFactory
            .select(Projections.constructor(DealDetailCacheValue.class,
                deal.id,
                product.id,
                product.name,
                deal.title,
                product.price,
                deal.discountPrice,
                deal.status,
                deal.startAt,
                deal.endAt))
            .from(deal)
            .join(deal.product, product)
            .where(deal.id.eq(dealId))
            .fetchOne();
    }
}
