package com.prj.flashdeal.domain.cart.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.prj.flashdeal.domain.cart.entity.CartItem;
import com.prj.flashdeal.domain.member.entity.Member;
import com.prj.flashdeal.domain.product.entity.Product;

public interface CartItemRepository extends JpaRepository<CartItem, Long>, CartItemRepositoryCustom {
    Optional<CartItem> findByMemberAndProduct(Member member, Product product);

    @Modifying
    @Query("DELETE FROM CartItem ci WHERE ci.member = :member")
    void deleteAllByMember(@Param("member") Member member);
}
