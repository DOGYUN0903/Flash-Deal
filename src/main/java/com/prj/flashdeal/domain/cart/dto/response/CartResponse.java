package com.prj.flashdeal.domain.cart.dto.response;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "장바구니 조회 응답")
public record CartResponse(
    @Schema(description = "회원 ID", example = "42") Long memberId,
    @Schema(description = "장바구니 항목 목록") List<CartItemResponse> items,
    @Schema(description = "장바구니 전체 합계 금액 (원)", example = "75000") Integer totalPrice,
    @Schema(description = "장바구니 전체 수량 합계", example = "5") Integer totalQuantity
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
