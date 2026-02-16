package com.prj.flashdeal.domain.deal.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.prj.flashdeal.domain.deal.dto.response.DealPurchaseResponse;
import com.prj.flashdeal.domain.deal.service.DealService;
import com.prj.flashdeal.global.response.ApiResponse;
import com.prj.flashdeal.global.security.CustomUserDetails;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/deals")
public class DealController {

    private final DealService dealService;

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
