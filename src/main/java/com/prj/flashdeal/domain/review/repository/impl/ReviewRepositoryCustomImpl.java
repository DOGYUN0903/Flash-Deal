package com.prj.flashdeal.domain.review.repository.impl;

import static com.prj.flashdeal.domain.review.entity.QReview.*;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import com.prj.flashdeal.domain.review.entity.Review;
import com.prj.flashdeal.domain.review.repository.ReviewRepositoryCustom;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ReviewRepositoryCustomImpl implements ReviewRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Review> findReviewsByProductId(Long productId, Pageable pageable) {

        List<Review> reviews = queryFactory
            .selectFrom(review)
            .where(
                review.product.id.eq(productId),
                review.isDeleted.isFalse()
            )
            .orderBy(review.createdAt.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        JPAQuery<Long> countQuery = queryFactory
            .select(review.count())
            .from(review)
            .where(
                review.product.id.eq(productId),
                review.isDeleted.isFalse()
            );

        return PageableExecutionUtils.getPage(reviews, pageable, countQuery::fetchOne);
    }
}
