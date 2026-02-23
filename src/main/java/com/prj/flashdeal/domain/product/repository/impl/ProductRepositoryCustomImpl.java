package com.prj.flashdeal.domain.product.repository.impl;

import static com.prj.flashdeal.domain.product.entity.QProduct.*;
import static com.prj.flashdeal.domain.review.entity.QReview.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.prj.flashdeal.domain.product.dto.request.ProductSearchCondForAdmin;
import com.prj.flashdeal.domain.product.dto.request.ProductSearchCondForUser;
import com.prj.flashdeal.domain.product.dto.response.ProductSummaryResponse;
import com.prj.flashdeal.domain.product.entity.ProductStatus;
import com.prj.flashdeal.domain.product.repository.ProductRepositoryCustom;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryCustomImpl implements ProductRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<ProductSummaryResponse> searchProductsForAdmin(ProductSearchCondForAdmin cond, Pageable pageable) {

        List<ProductSummaryResponse> productSummaryResponseList = queryFactory
            .select(Projections.constructor(ProductSummaryResponse.class,
                product.id,
                product.name,
                product.price,
                product.stockQuantity,
                product.status,
                JPAExpressions.select(review.id.count())
                    .from(review)
                    .where(review.product.id.eq(product.id), review.isDeleted.isFalse()),
                JPAExpressions.select(review.rating.avg())
                    .from(review)
                    .where(review.product.id.eq(product.id), review.isDeleted.isFalse())))
            .from(product)
            .where(
                productNameContains(cond.getProductName()),
                priceGoe(cond.getMinPrice()),
                priceLoe(cond.getMaxPrice()),
                registeredAtGoe(cond.getStartDate()),
                registeredAtLoe(cond.getEndDate()),
                statusEq(cond.getStatus()),
                isDeletedEq(cond.getIsDeleted())
            )
            .orderBy(product.createdAt.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        JPAQuery<Long> countQuery = queryFactory
            .select(product.count())
            .from(product)
            .where(
                productNameContains(cond.getProductName()),
                priceGoe(cond.getMinPrice()),
                priceLoe(cond.getMaxPrice()),
                registeredAtGoe(cond.getStartDate()),
                registeredAtLoe(cond.getEndDate()),
                statusEq(cond.getStatus()),
                isDeletedEq(cond.getIsDeleted())
            );

        return PageableExecutionUtils.getPage(productSummaryResponseList, pageable, countQuery::fetchOne);
    }

    @Override
    public Page<ProductSummaryResponse> searchProductsForUser(ProductSearchCondForUser cond, Pageable pageable) {

        List<ProductSummaryResponse> productSummaryResponseList = queryFactory
            .select(Projections.constructor(ProductSummaryResponse.class,
                product.id,
                product.name,
                product.price,
                product.stockQuantity,
                product.status,
                JPAExpressions.select(review.id.count())
                    .from(review)
                    .where(review.product.id.eq(product.id), review.isDeleted.isFalse()),
                JPAExpressions.select(review.rating.avg())
                    .from(review)
                    .where(review.product.id.eq(product.id), review.isDeleted.isFalse())))
            .from(product)
            .where(
                product.isDeleted.isFalse(),
                product.status.in(ProductStatus.ON_SALE, ProductStatus.SOLD_OUT),
                productNameContains(cond.getProductName()),
                priceGoe(cond.getMinPrice()),
                priceLoe(cond.getMaxPrice())
            )
            .orderBy(product.createdAt.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        JPAQuery<Long> countQuery = queryFactory
            .select(product.count())
            .from(product)
            .where(
                product.isDeleted.isFalse(),
                product.status.in(ProductStatus.ON_SALE, ProductStatus.SOLD_OUT),
                productNameContains(cond.getProductName()),
                priceGoe(cond.getMinPrice()),
                priceLoe(cond.getMaxPrice())
            );

        return PageableExecutionUtils.getPage(productSummaryResponseList, pageable, countQuery::fetchOne);
    }

    private BooleanExpression productNameContains(String productName) {
        if (!StringUtils.hasText(productName)) {
            return null;
        }
        return product.name.containsIgnoreCase(productName);
    }

    private BooleanExpression priceGoe(Integer minPrice) {
        return minPrice != null ? product.price.goe(minPrice) : null;
    }

    private BooleanExpression priceLoe(Integer maxPrice) {
        return maxPrice != null ? product.price.loe(maxPrice) : null;
    }

    private BooleanExpression registeredAtGoe(LocalDate startDate) {
        return startDate != null ? product.createdAt.goe(startDate.atStartOfDay()) : null;
    }

    private BooleanExpression registeredAtLoe(LocalDate endDate) {
        return endDate != null ? product.createdAt.loe(endDate.atTime(LocalTime.MAX)) : null;
    }

    private BooleanExpression statusEq(ProductStatus status) {
        return status != null ? product.status.eq(status) : null;
    }

    private BooleanExpression isDeletedEq(Boolean isDeleted) {
        if (isDeleted == null) {
            return null;
        }
        return isDeleted ? product.isDeleted.isTrue() : product.isDeleted.isFalse();
    }
}
