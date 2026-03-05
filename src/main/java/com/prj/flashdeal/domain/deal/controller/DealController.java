package com.prj.flashdeal.domain.deal.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.prj.flashdeal.domain.deal.dto.request.DealOrderRequest;
import com.prj.flashdeal.domain.deal.dto.response.DealResponse;
import com.prj.flashdeal.domain.deal.service.DealService;
import com.prj.flashdeal.domain.order.dto.response.OrderResponse;
import com.prj.flashdeal.global.response.ApiResponse;
import com.prj.flashdeal.global.security.CustomUserDetails;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Deal", description = "딜 API")
@RestController
@RequestMapping("/api/deals")
@RequiredArgsConstructor
public class DealController {

    private final DealService dealService;

    @Operation(summary = "딜 목록 조회")
    @GetMapping
    public ResponseEntity<ApiResponse<List<DealResponse>>> getDeals() {
        return ApiResponse.success(HttpStatus.OK, "딜 목록 조회 성공", dealService.getDeals());
    }

    @Operation(summary = "딜 단건 조회")
    @GetMapping("/{dealId}")
    public ResponseEntity<ApiResponse<DealResponse>> getDeal(@PathVariable Long dealId) {
        return ApiResponse.success(HttpStatus.OK, "딜 조회 성공", dealService.getDeal(dealId));
    }

    @Operation(summary = "선착순 딜 주문")
    @PostMapping("/{dealId}/order")
    public ResponseEntity<ApiResponse<OrderResponse>> createDealOrder(
        @PathVariable Long dealId,
        @RequestBody @Valid DealOrderRequest request,
        @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        OrderResponse response = dealService.createDealOrder(userDetails.getUserId(), dealId, request);
        return ApiResponse.success(HttpStatus.CREATED, "딜 주문 성공", response);
    }
}
