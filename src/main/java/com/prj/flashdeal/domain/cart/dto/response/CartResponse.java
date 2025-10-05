package com.prj.flashdeal.domain.cart.dto.response;

import java.util.List;


public record CartResponse(
    Long memberId,
    List<CartItemResponse> items,
    Integer totalPrice,
    Integer totalQuantity
) {
    public static CartResponse of(Long memberId, List<CartItemResponse> items) {
        int totalPrice = items.stream()
            .mapToInt(CartItemResponse::totalPrice)
            .sum();

        int totalQuantity = items.stream()
            .mapToInt(CartItemResponse::quantity)
            .sum();

        return new CartResponse(memberId, items, totalPrice, totalQuantity);
    }
}
