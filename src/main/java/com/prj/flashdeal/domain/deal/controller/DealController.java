package com.prj.flashdeal.domain.deal.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.prj.flashdeal.domain.deal.dto.response.DealDetailResponse;
import com.prj.flashdeal.domain.deal.dto.response.DealPurchaseResponse;
import com.prj.flashdeal.domain.deal.dto.response.DealSummaryResponse;
import com.prj.flashdeal.domain.deal.service.DealService;
import com.prj.flashdeal.global.response.ApiResponse;
import com.prj.flashdeal.global.response.PageResponse;
import com.prj.flashdeal.global.security.CustomUserDetails;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/deals")
public class DealController {

    private final DealService dealService;

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<DealSummaryResponse>>> getDeals(
            @PageableDefault(size = 10) Pageable pageable
    ) {
        return ApiResponse.success(
                HttpStatus.OK,
                "딜 목록 조회가 완료되었습니다.",
                dealService.getDeals(pageable)
        );
    }

    @GetMapping("/{dealId}")
    public ResponseEntity<ApiResponse<DealDetailResponse>> getDeal(
            @PathVariable Long dealId
    ) {
        return ApiResponse.success(
                HttpStatus.OK,
                "딜 상세 조회가 완료되었습니다.",
                dealService.getDeal(dealId)
        );
    }

    @PostMapping("/{dealId}/purchase")
    public ResponseEntity<ApiResponse<DealPurchaseResponse>> purchase(
            @AuthenticationPrincipal CustomUserDetails userPrincipal,
            @PathVariable Long dealId
    ) {
        return ApiResponse.success(
                HttpStatus.CREATED,
                "선착순 구매가 완료되었습니다.",
                dealService.purchase(userPrincipal.getUserId(), dealId)
        );
    }
}
