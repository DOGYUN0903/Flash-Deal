package com.prj.flashdeal.domain.cart.repository.impl;

import static com.prj.flashdeal.domain.cart.entity.QCartItem.*;
import static com.prj.flashdeal.domain.product.entity.QProduct.*;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.prj.flashdeal.domain.cart.dto.response.CartItemResponse;
import com.prj.flashdeal.domain.cart.repository.CartItemRepositoryCustom;
import com.prj.flashdeal.domain.member.entity.Member;
import com.prj.flashdeal.domain.product.entity.QProduct;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CartItemRepositoryCustomImpl implements CartItemRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<CartItemResponse> findCartItemsByMember(Member member) {
        return queryFactory
            .select(Projections.constructor(CartItemResponse.class,
                cartItem.id,
                product.id,
                product.name,
                cartItem.price,
                cartItem.quantity,
                cartItem.price.multiply(cartItem.quantity))
            )
            .from(cartItem)
            .join(cartItem.product, product)
            .where(cartItem.member.eq(member))
            .fetch();
    }
}
