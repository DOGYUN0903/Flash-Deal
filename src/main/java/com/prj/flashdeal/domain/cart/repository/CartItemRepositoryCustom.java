package com.prj.flashdeal.domain.cart.repository;

import java.util.List;

import com.prj.flashdeal.domain.cart.dto.response.CartItemResponse;
import com.prj.flashdeal.domain.member.entity.Member;

public interface CartItemRepositoryCustom {
    List<CartItemResponse> findCartItemsByMember(Member member);
}
