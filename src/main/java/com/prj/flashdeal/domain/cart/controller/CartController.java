package com.prj.flashdeal.domain.cart.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.prj.flashdeal.domain.cart.dto.request.CartItemAddRequest;
import com.prj.flashdeal.domain.cart.dto.request.CartItemUpdateRequest;
import com.prj.flashdeal.domain.cart.dto.response.CartItemResponse;
import com.prj.flashdeal.domain.cart.dto.response.CartResponse;
import com.prj.flashdeal.domain.cart.service.CartService;
import com.prj.flashdeal.global.response.ApiResponse;
import com.prj.flashdeal.global.security.CustomUserDetails;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/carts")
public class CartController {

    private final CartService cartService;

    @PostMapping
    public ResponseEntity<ApiResponse<CartItemResponse>> addCartItem(
        @AuthenticationPrincipal CustomUserDetails userPrincipal,
        @Valid @RequestBody CartItemAddRequest request
    ) {
        return ApiResponse.success(
            HttpStatus.CREATED,
            "장바구니에 상품이 추가되었습니다.",
            cartService.addCartItem(userPrincipal.getUserId(), request)
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<CartResponse>> getCartItems(
        @AuthenticationPrincipal CustomUserDetails userPrincipal) {
        return ApiResponse.success(
            HttpStatus.OK,
            "장바구니 조회가 완료되었습니다.",
            cartService.getCartItems(userPrincipal.getUserId())
        );
    }

    @PatchMapping("/{cartItemId}")
    public ResponseEntity<ApiResponse<CartItemResponse>> updateCartItemQuantity(
        @AuthenticationPrincipal CustomUserDetails userPrincipal,
        @PathVariable Long cartItemId,
        @Valid @RequestBody CartItemUpdateRequest request
    ) {
        return ApiResponse.success(
            HttpStatus.OK,
            "장바구니 상품 수량이 수정되었습니다.",
            cartService.updateCartItemQuantity(userPrincipal.getUserId(), cartItemId, request)
        );
    }

    @DeleteMapping("/{cartItemId}")
    public ResponseEntity<ApiResponse<Void>> deleteCartItem(
        @AuthenticationPrincipal CustomUserDetails userPrincipal,
        @PathVariable Long cartItemId
    ) {
        cartService.deleteCartItem(userPrincipal.getUserId(), cartItemId);

        return ApiResponse.success(
            HttpStatus.OK,
            "장바구니 상품이 삭제되었습니다.",
            null
        );
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> clearCart(
        @AuthenticationPrincipal CustomUserDetails userPrincipal
    ) {
        cartService.clearCart(userPrincipal.getUserId());

        return ApiResponse.success(
            HttpStatus.OK,
            "장바구니가 비워졌습니다.",
            null
        );
    }
}
