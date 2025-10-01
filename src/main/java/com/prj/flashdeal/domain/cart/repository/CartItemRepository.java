package com.prj.flashdeal.domain.cart.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.prj.flashdeal.domain.cart.entity.CartItem;
import com.prj.flashdeal.domain.member.entity.Member;
import com.prj.flashdeal.domain.product.entity.Product;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    Optional<CartItem> findByMemberAndProduct(Member member, Product product);
}
