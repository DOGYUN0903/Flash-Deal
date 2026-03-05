package com.prj.flashdeal.domain.deal.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.prj.flashdeal.domain.deal.dto.request.DealCreateRequest;
import com.prj.flashdeal.domain.deal.dto.response.DealResponse;
import com.prj.flashdeal.domain.deal.service.DealService;
import com.prj.flashdeal.global.response.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Admin Deal", description = "딜 관리 API (어드민 전용)")
@RestController
@RequestMapping("/api/admin/deals")
@RequiredArgsConstructor
public class AdminDealController {

    private final DealService dealService;

    @Operation(summary = "딜 등록")
    @PostMapping
    public ResponseEntity<ApiResponse<DealResponse>> createDeal(
        @RequestBody @Valid DealCreateRequest request
    ) {
        return ApiResponse.success(HttpStatus.CREATED, "딜 등록 성공", dealService.createDeal(request));
    }

}
