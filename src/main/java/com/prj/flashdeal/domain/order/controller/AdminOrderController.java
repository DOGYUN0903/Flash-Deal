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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Admin - Order", description = "주문 관리 API (어드민)")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/orders")
public class AdminOrderController {

    private final OrderService orderService;

    /**
     * 전체 주문 목록 조회 (관리자) - 페이징
     */
    @Operation(summary = "전체 주문 목록 조회", description = "모든 회원의 주문 목록을 최신순으로 조회합니다. (페이징, 어드민 전용)")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "주문 목록 조회 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 필요"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "권한 없음")
    })
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
    @Operation(summary = "주문 상세 조회", description = "주문 ID로 특정 주문의 상세 정보를 조회합니다. (어드민 전용)")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "주문 상세 조회 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 필요"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "권한 없음"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "존재하지 않는 주문")
    })
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
    @Operation(summary = "배송 시작 처리", description = "PAID 상태의 주문을 배송 중(SHIPPING) 상태로 변경합니다. (어드민 전용)")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "배송 시작 처리 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 주문 상태"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 필요"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "권한 없음"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "존재하지 않는 주문")
    })
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
    @Operation(summary = "배송 완료 처리", description = "SHIPPING 상태의 주문을 배송 완료(DELIVERED) 상태로 변경합니다. (어드민 전용)")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "배송 완료 처리 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 주문 상태"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 필요"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "권한 없음"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "존재하지 않는 주문")
    })
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
