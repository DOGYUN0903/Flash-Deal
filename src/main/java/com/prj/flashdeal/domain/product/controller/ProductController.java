package com.prj.flashdeal.domain.product.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.prj.flashdeal.domain.product.dto.request.ProductSearchCondForUser;
import com.prj.flashdeal.domain.product.dto.response.ProductResponseForUser;
import com.prj.flashdeal.domain.product.dto.response.ProductSummaryResponse;
import com.prj.flashdeal.domain.product.service.ProductService;
import com.prj.flashdeal.global.response.ApiResponse;
import com.prj.flashdeal.global.response.PageResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<ProductSummaryResponse>>> searchProductsForUser(
        @ModelAttribute ProductSearchCondForUser cond,
        @PageableDefault(size = 10) Pageable pageable
    ) {
        return ApiResponse.success(
            HttpStatus.OK,
            "상품 검색이 완료되었습니다.",
            productService.searchProductsForUser(cond, pageable)
        );
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ApiResponse<ProductResponseForUser>> getProductForUser(
        @PathVariable Long productId
    ) {
        return ApiResponse.success(
            HttpStatus.OK,
            "상품 상세 조회가 완료되었습니다.",
            productService.getProductForUser(productId)
        );
    }
}
