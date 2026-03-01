package com.prj.flashdeal.domain.order.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.prj.flashdeal.domain.order.dto.request.OrderCreateRequest;
import com.prj.flashdeal.domain.order.dto.response.OrderResponse;
import com.prj.flashdeal.domain.order.dto.response.OrderSummaryResponse;
import com.prj.flashdeal.domain.order.service.OrderService;
import com.prj.flashdeal.global.response.ApiResponse;
import com.prj.flashdeal.global.response.PageResponse;
import com.prj.flashdeal.global.security.CustomUserDetails;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    /**
     * 장바구니에서 주문 생성
     */
    @PostMapping("/from-cart")
    public ResponseEntity<ApiResponse<OrderResponse>> createOrderFromCart(
        @AuthenticationPrincipal CustomUserDetails userPrincipal
    ) {
        return ApiResponse.success(
            HttpStatus.CREATED,
            "주문이 생성되었습니다.",
            orderService.createOrderFromCart(userPrincipal.getUserId())
        );
    }

    /**
     * 바로 구매 (장바구니 거치지 않음)
     */
    @PostMapping("/direct")
    public ResponseEntity<ApiResponse<OrderResponse>> createDirectOrder(
        @AuthenticationPrincipal CustomUserDetails userPrincipal,
        @Valid @RequestBody OrderCreateRequest request
    ) {
        return ApiResponse.success(
            HttpStatus.CREATED,
            "바로 구매가 완료되었습니다.",
            orderService.createDirectOrder(userPrincipal.getUserId(), request)
        );
    }

    /**
     * 주문 단건 조회
     */
    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrder(
        @AuthenticationPrincipal CustomUserDetails userPrincipal,
        @PathVariable Long orderId
    ) {
        return ApiResponse.success(
            HttpStatus.OK,
            "주문 조회가 완료되었습니다.",
            orderService.getOrder(userPrincipal.getUserId(), orderId)
        );
    }

    /**
     * 주문 목록 조회 - 페이징
     */
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<OrderSummaryResponse>>> getOrders(
        @AuthenticationPrincipal CustomUserDetails userPrincipal,
        @PageableDefault(size = 10) Pageable pageable
    ) {
        return ApiResponse.success(
            HttpStatus.OK,
            "주문 목록 조회가 완료되었습니다.",
            orderService.getOrders(userPrincipal.getUserId(), pageable)
        );
    }

    /**
     * 주문 취소
     */
    @DeleteMapping("/{orderId}")
    public ResponseEntity<ApiResponse<Void>> cancelOrder(
        @AuthenticationPrincipal CustomUserDetails userPrincipal,
        @PathVariable Long orderId
    ) {
        orderService.cancelOrder(userPrincipal.getUserId(), orderId);
        return ApiResponse.success(
            HttpStatus.OK,
            "주문이 취소되었습니다.",
            null
        );
    }
}
