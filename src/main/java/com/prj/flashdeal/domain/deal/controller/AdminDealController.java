package com.prj.flashdeal.domain.deal.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.prj.flashdeal.domain.deal.dto.request.DealCreateRequest;
import com.prj.flashdeal.domain.deal.dto.response.DealDetailResponse;
import com.prj.flashdeal.domain.deal.service.DealService;
import com.prj.flashdeal.global.response.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/deals")
public class AdminDealController {

    private final DealService dealService;

    @PostMapping
    public ResponseEntity<ApiResponse<DealDetailResponse>> createDeal(
            @Valid @RequestBody DealCreateRequest request
    ) {
        return ApiResponse.success(
                HttpStatus.CREATED,
                "딜이 등록되었습니다.",
                dealService.createDeal(request)
        );
    }
}
