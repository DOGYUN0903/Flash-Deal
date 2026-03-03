package com.prj.flashdeal.domain.review.controller;

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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.prj.flashdeal.domain.review.dto.request.ReviewRequest;
import com.prj.flashdeal.domain.review.dto.response.ReviewResponse;
import com.prj.flashdeal.domain.review.service.ReviewService;
import com.prj.flashdeal.global.response.ApiResponse;
import com.prj.flashdeal.global.response.PageResponse;
import com.prj.flashdeal.global.security.CustomUserDetails;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Review", description = "리뷰 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products/{productId}/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    @Operation(summary = "리뷰 작성", description = "구매한 상품에 대해 리뷰를 작성합니다. 구매 이력이 없거나 이미 리뷰를 작성한 경우 실패합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "리뷰 작성 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "유효성 검사 실패 또는 이미 작성한 리뷰"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 필요"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "구매 이력 없음"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "존재하지 않는 상품")
    })
    @PostMapping
    public ResponseEntity<ApiResponse<ReviewResponse>> createReview(
        @PathVariable Long productId,
        @RequestBody @Valid ReviewRequest request,
        @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return ApiResponse.success(
            HttpStatus.CREATED,
            "리뷰가 등록되었습니다.",
            reviewService.createReview(userDetails.getUserId(), productId, request)
        );
    }

    @Operation(summary = "상품 리뷰 목록 조회", description = "특정 상품의 리뷰 목록을 조회합니다. (페이징)")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "리뷰 목록 조회 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "존재하지 않는 상품")
    })
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<ReviewResponse>>> getReviews(
        @PathVariable Long productId,
        @PageableDefault(size = 10) Pageable pageable
    ) {
        return ApiResponse.success(
            HttpStatus.OK,
            "리뷰 목록 조회가 완료되었습니다.",
            reviewService.getReviews(productId, pageable)
        );
    }
}
