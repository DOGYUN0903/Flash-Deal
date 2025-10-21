package com.prj.flashdeal.domain.payment.repository.impl;

import static com.prj.flashdeal.domain.payment.entity.QPayment.*;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.prj.flashdeal.domain.payment.entity.Payment;
import com.prj.flashdeal.domain.payment.repository.PaymentRepositoryCustom;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class PaymentRepositoryCustomImpl implements PaymentRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<Payment> findByOrderId(Long orderId) {
        Payment result = queryFactory
            .selectFrom(payment)
            .where(payment.order.id.eq(orderId))
            .fetchOne();

        return Optional.ofNullable(result);
    }
}
