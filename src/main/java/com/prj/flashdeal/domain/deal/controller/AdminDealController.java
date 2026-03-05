package com.prj.flashdeal.domain.deal.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.prj.flashdeal.domain.deal.dto.request.DealCreateRequest;
import com.prj.flashdeal.domain.deal.dto.response.DealResponse;
import com.prj.flashdeal.domain.deal.service.DealService;
import com.prj.flashdeal.global.response.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Admin Deal", description = "딜 관리 API (어드민 전용)")
@RestController
@RequestMapping("/api/admin/deals")
@RequiredArgsConstructor
public class AdminDealController {

    private final DealService dealService;

    @Operation(summary = "딜 등록", description = "새로운 플래시 딜을 등록합니다. 상품 ID와 할인 가격, 딜 시간을 지정합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "딜 등록 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "유효성 검사 실패"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 필요"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "어드민 권한 필요"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "존재하지 않는 상품")
    })
    @PostMapping
    public ResponseEntity<ApiResponse<DealResponse>> createDeal(
        @RequestBody @Valid DealCreateRequest request
    ) {
        return ApiResponse.success(HttpStatus.CREATED, "딜 등록 성공", dealService.createDeal(request));
    }

}
