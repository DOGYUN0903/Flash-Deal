package com.prj.flashdeal.domain.deal.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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
import com.prj.flashdeal.global.response.PageResponse;
import com.prj.flashdeal.global.security.CustomUserDetails;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Deal", description = "딜 API")
@RestController
@RequestMapping("/api/deals")
@RequiredArgsConstructor
public class DealController {

    private final DealService dealService;

    @Operation(summary = "딜 목록 조회", description = "현재 등록된 모든 딜 목록을 페이징으로 조회합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "딜 목록 조회 성공")
    })
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<DealResponse>>> getDeals(
        @PageableDefault(size = 10) Pageable pageable
    ) {
        return ApiResponse.success(HttpStatus.OK, "딜 목록 조회 성공", dealService.getDeals(pageable));
    }

    @Operation(summary = "딜 단건 조회", description = "딜 ID로 딜 상세 정보와 잔여 재고를 조회합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "딜 조회 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "존재하지 않는 딜")
    })
    @GetMapping("/{dealId}")
    public ResponseEntity<ApiResponse<DealResponse>> getDeal(
        @Parameter(description = "딜 ID", example = "1") @PathVariable Long dealId
    ) {
        return ApiResponse.success(HttpStatus.OK, "딜 조회 성공", dealService.getDeal(dealId));
    }

    @Operation(summary = "선착순 딜 주문", description = "ACTIVE 상태의 딜에 선착순 주문을 요청합니다. 토스페이먼츠 결제 승인 후 주문이 생성됩니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "딜 주문 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "결제 금액 불일치 또는 유효성 검사 실패"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 필요"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "존재하지 않는 딜 또는 재고 없음"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "딜이 ACTIVE 상태가 아님")
    })
    @PostMapping("/{dealId}/order")
    public ResponseEntity<ApiResponse<OrderResponse>> createDealOrder(
        @Parameter(description = "딜 ID", example = "1") @PathVariable Long dealId,
        @RequestBody @Valid DealOrderRequest request,
        @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        OrderResponse response = dealService.createDealOrder(userDetails.getUserId(), dealId, request);
        return ApiResponse.success(HttpStatus.CREATED, "딜 주문 성공", response);
    }
}
