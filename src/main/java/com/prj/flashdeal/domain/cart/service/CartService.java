package com.prj.flashdeal.domain.cart.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prj.flashdeal.domain.cart.dto.request.CartItemAddRequest;
import com.prj.flashdeal.domain.cart.dto.response.CartItemResponse;
import com.prj.flashdeal.domain.cart.dto.response.CartResponse;
import com.prj.flashdeal.domain.cart.entity.CartItem;
import com.prj.flashdeal.domain.cart.repository.CartItemRepository;
import com.prj.flashdeal.domain.member.entity.Member;
import com.prj.flashdeal.domain.member.service.MemberService;
import com.prj.flashdeal.domain.product.entity.Product;
import com.prj.flashdeal.domain.product.service.ProductService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final MemberService memberService;
    private final ProductService productService;

    @Transactional
    public CartItemResponse addCartItem(Long memberId, CartItemAddRequest request) {
        Member member = memberService.getMember(memberId);

        Product product = productService.findCartableProduct(request.getProductId());

        CartItem cartItem = findOrCreateCartItem(member, product, request.getQuantity());

        return CartItemResponse.from(cartItem);
    }

    @Transactional(readOnly = true)
    public CartResponse getCartItems(Long memberId) {

        Member member = memberService.getMember(memberId);

        List<CartItemResponse> cartItems = cartItemRepository.findCartItemsByMember(member);

        return CartResponse.of(member.getId(), cartItems);
    }

    // ---------------- private 헬퍼 메서드 ----------------
    private CartItem findOrCreateCartItem(Member member, Product product, int quantity) {
        return cartItemRepository.findByMemberAndProduct(member, product)
            .map(existingItem -> {
                existingItem.addQuantity(quantity);
                return existingItem;
            })
            .orElseGet(() -> {
                CartItem cartItem = CartItem.builder()
                    .member(member)
                    .product(product)
                    .quantity(quantity)
                    .build();
                return cartItemRepository.save(cartItem);
            });
    }
}
