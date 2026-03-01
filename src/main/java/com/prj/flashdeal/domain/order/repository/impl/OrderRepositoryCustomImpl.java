package com.prj.flashdeal.domain.order.repository.impl;

import static com.prj.flashdeal.domain.order.entity.QOrder.*;
import static com.prj.flashdeal.domain.order.entity.QOrderItem.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import com.prj.flashdeal.domain.member.entity.Member;
import com.prj.flashdeal.domain.order.dto.response.OrderSummaryResponse;
import com.prj.flashdeal.domain.order.entity.Order;
import com.prj.flashdeal.domain.order.entity.OrderStatus;
import com.prj.flashdeal.domain.order.repository.OrderRepositoryCustom;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryCustomImpl implements OrderRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<Order> findByIdAndMemberId(Long orderId, Long memberId) {
        Order result = queryFactory
            .selectFrom(order)
            .where(
                order.id.eq(orderId),
                order.member.id.eq(memberId)
            )
            .fetchOne();

        return Optional.ofNullable(result);
    }

    @Override
    public List<Order> findAllByMemberOrderByCreatedAtDesc(Member member) {
        return queryFactory
            .selectFrom(order)
            .where(order.member.eq(member))
            .orderBy(order.createdAt.desc())
            .fetch();
    }

    @Override
    public Page<OrderSummaryResponse> findAllOrderSummaries(Pageable pageable) {
        List<OrderSummaryResponse> content = queryFactory
            .select(Projections.constructor(OrderSummaryResponse.class,
                order.id,
                order.status,
                order.totalPrice,
                orderItem.count().intValue(),
                order.createdAt
            ))
            .from(order)
            .leftJoin(order.orderItems, orderItem)
            .groupBy(order.id, order.status, order.totalPrice, order.createdAt)
            .orderBy(order.createdAt.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        JPAQuery<Long> countQuery = queryFactory
            .select(order.count())
            .from(order);

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    @Override
    public Page<OrderSummaryResponse> findOrderSummariesByMember(Member member, Pageable pageable) {
        List<OrderSummaryResponse> content = queryFactory
            .select(Projections.constructor(OrderSummaryResponse.class,
                order.id,
                order.status,
                order.totalPrice,
                orderItem.count().intValue(),
                order.createdAt
            ))
            .from(order)
            .leftJoin(order.orderItems, orderItem)
            .where(order.member.eq(member))
            .groupBy(order.id, order.status, order.totalPrice, order.createdAt)
            .orderBy(order.createdAt.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        JPAQuery<Long> countQuery = queryFactory
            .select(order.count())
            .from(order)
            .where(order.member.eq(member));

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    @Override
    public List<Order> findExpiredPendingOrders(LocalDateTime expiredBefore) {
        return queryFactory
            .selectFrom(order)
            .join(order.orderItems, orderItem).fetchJoin()
            .where(
                order.status.eq(OrderStatus.PENDING),
                order.createdAt.lt(expiredBefore)
            )
            .fetch();
    }

    @Override
    public boolean existsPurchasedProduct(Long memberId, Long productId) {
        return queryFactory
            .selectOne()
            .from(orderItem)
            .where(
                orderItem.order.member.id.eq(memberId),
                orderItem.product.id.eq(productId),
                orderItem.order.status.in(OrderStatus.PAID, OrderStatus.SHIPPED, OrderStatus.DELIVERED)
            )
            .fetchFirst() != null;
    }
}
