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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Order", description = "주문 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    /**
     * 장바구니에서 주문 생성
     */
    @Operation(summary = "장바구니 주문", description = "장바구니에 담긴 상품을 주문합니다. 주문 후 장바구니는 자동으로 비워집니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "주문 생성 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "장바구니가 비어있음"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 필요")
    })
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
    @Operation(summary = "바로 구매", description = "장바구니 없이 특정 상품을 바로 주문합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "주문 생성 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "유효성 검사 실패 또는 재고 부족"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 필요"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "존재하지 않는 상품")
    })
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
    @Operation(summary = "주문 단건 조회", description = "주문 ID로 주문 상세 정보를 조회합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "주문 조회 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 필요"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "존재하지 않는 주문")
    })
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
    @Operation(summary = "내 주문 목록", description = "내 주문 목록을 최신순으로 조회합니다. (페이징)")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "주문 목록 조회 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 필요")
    })
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
    @Operation(summary = "주문 취소", description = "PENDING 상태의 주문을 취소합니다. 재고가 복구됩니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "주문 취소 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "취소 불가능한 상태"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 필요"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "존재하지 않는 주문")
    })
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
