package com.prj.flashdeal.domain.cart.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prj.flashdeal.domain.cart.dto.request.CartItemAddRequest;
import com.prj.flashdeal.domain.cart.dto.request.CartItemUpdateRequest;
import com.prj.flashdeal.domain.cart.dto.response.CartItemResponse;
import com.prj.flashdeal.domain.cart.dto.response.CartResponse;
import com.prj.flashdeal.domain.cart.entity.CartItem;
import com.prj.flashdeal.domain.cart.exception.CartErrorCode;
import com.prj.flashdeal.domain.cart.exception.CartException;
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

    @Transactional
    public CartItemResponse updateCartItemQuantity(Long memberId, Long cartItemId, CartItemUpdateRequest request) {

        Member member = memberService.getMember(memberId);
        CartItem cartItem = getCartItem(cartItemId);

        validateCartItemOwner(cartItem, member);

        cartItem.updateQuantity(request.getQuantity());

        return CartItemResponse.from(cartItem);
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

    private CartItem getCartItem(Long cartItemId) {
        return cartItemRepository.findById(cartItemId)
            .orElseThrow(() -> new CartException(CartErrorCode.CART_ITEM_NOT_FOUND));
    }

    private void validateCartItemOwner(CartItem cartItem, Member member) {
        if (!cartItem.getMember().getId().equals(member.getId())) {
            throw new CartException(CartErrorCode.UNAUTHORIZED_CART_ITEM);
        }
    }
}
