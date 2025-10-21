package com.prj.flashdeal.domain.order.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.prj.flashdeal.domain.order.dto.response.OrderResponse;
import com.prj.flashdeal.domain.order.dto.response.OrderSummaryResponse;
import com.prj.flashdeal.domain.order.service.OrderService;
import com.prj.flashdeal.global.response.ApiResponse;
import com.prj.flashdeal.global.response.PageResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/orders")
public class AdminOrderController {

    private final OrderService orderService;

    /**
     * 전체 주문 목록 조회 (관리자) - 페이징
     */
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<OrderSummaryResponse>>> getAllOrders(
        @PageableDefault(size = 10) Pageable pageable
    ) {
        return ApiResponse.success(
            HttpStatus.OK,
            "전체 주문 목록 조회가 완료되었습니다.",
            orderService.getAllOrders(pageable)
        );
    }

    /**
     * 주문 상세 조회 (관리자)
     */
    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrder(
        @PathVariable Long orderId
    ) {
        return ApiResponse.success(
            HttpStatus.OK,
            "주문 상세 조회가 완료되었습니다.",
            orderService.getOrderForAdmin(orderId)
        );
    }

    /**
     * 배송 시작 처리 (관리자)
     */
    @PatchMapping("/{orderId}/ship")
    public ResponseEntity<ApiResponse<Void>> startShipping(
        @PathVariable Long orderId
    ) {
        orderService.startShipping(orderId);
        return ApiResponse.success(
            HttpStatus.OK,
            "배송이 시작되었습니다.",
            null
        );
    }

    /**
     * 배송 완료 처리 (관리자)
     */
    @PatchMapping("/{orderId}/deliver")
    public ResponseEntity<ApiResponse<Void>> completeDelivery(
        @PathVariable Long orderId
    ) {
        orderService.completeDelivery(orderId);
        return ApiResponse.success(
            HttpStatus.OK,
            "배송이 완료되었습니다.",
            null
        );
    }
}
