package com.prj.flashdeal.domain.cart.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.prj.flashdeal.domain.cart.dto.request.CartItemAddRequest;
import com.prj.flashdeal.domain.cart.dto.response.CartItemResponse;
import com.prj.flashdeal.domain.cart.service.CartService;
import com.prj.flashdeal.global.response.ApiResponse;
import com.prj.flashdeal.global.security.dto.UserPrincipal;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/carts")
public class CartController {

    private final CartService cartService;

    @PostMapping
    public ResponseEntity<ApiResponse<CartItemResponse>> addCartItem(
        @AuthenticationPrincipal UserPrincipal userPrincipal,
        @Valid @RequestBody CartItemAddRequest request
    ) {
        return ApiResponse.success(
            HttpStatus.CREATED,
            "장바구니에 상품이 추가되었습니다.",
            cartService.addCartItem(userPrincipal.getUserId(), request)
        );
    }
}
