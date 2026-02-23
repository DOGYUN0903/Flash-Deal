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

import com.prj.flashdeal.domain.review.dto.request.ReviewRequest;
import com.prj.flashdeal.domain.review.dto.response.ReviewResponse;
import com.prj.flashdeal.domain.review.service.ReviewService;
import com.prj.flashdeal.global.response.ApiResponse;
import com.prj.flashdeal.global.response.PageResponse;
import com.prj.flashdeal.global.security.CustomUserDetails;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products/{productId}/reviews")
public class ReviewController {

    private final ReviewService reviewService;

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
