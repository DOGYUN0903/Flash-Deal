package com.prj.flashdeal.domain.product.controller;


import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.prj.flashdeal.domain.product.dto.request.ProductCreateRequest;
import com.prj.flashdeal.domain.product.dto.request.ProductSearchCondForAdmin;
import com.prj.flashdeal.domain.product.dto.request.ProductUpdateRequest;
import com.prj.flashdeal.domain.product.dto.request.StockAddRequest;
import com.prj.flashdeal.domain.product.dto.response.ProductResponse;
import com.prj.flashdeal.domain.product.dto.response.ProductSummaryResponse;
import com.prj.flashdeal.domain.product.service.ProductService;
import com.prj.flashdeal.global.response.ApiResponse;
import com.prj.flashdeal.global.response.PageResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/products")
public class AdminProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(
        @Valid @RequestBody ProductCreateRequest request) {
        return ApiResponse.success(
            HttpStatus.CREATED,
            "상품 등록이 완료되었습니다.",
            productService.createProduct(request)
        );
    }

    @PostMapping("/{productId}/stock")
    public ResponseEntity<ApiResponse<ProductResponse>> addStock(
        @PathVariable Long productId,
        @Valid @RequestBody StockAddRequest request
    ) {
        return ApiResponse.success(
            HttpStatus.OK,
            "재고가 추가되었습니다.",
            productService.addStock(productId, request)
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<ProductSummaryResponse>>> searchProductsForAdmin(
        @ModelAttribute ProductSearchCondForAdmin cond,
        @PageableDefault(size = 10) Pageable pageable
    ) {
        return ApiResponse.success(
            HttpStatus.OK,
            "상품 검색이 완료되었습니다.",
            productService.searchProductsForAdmin(cond, pageable)
        );
    }

    @PatchMapping("/{productId}")
    public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(
        @PathVariable Long productId,
        @RequestBody ProductUpdateRequest request
    ) {
        return ApiResponse.success(
            HttpStatus.OK,
            "상품 수정이 완료되었습니다.",
            productService.updateProduct(productId, request)
        );
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(
        @PathVariable Long productId
    ) {
        productService.deleteProduct(productId);

        return ApiResponse.success(
            HttpStatus.OK,
            "상품이 삭제되었습니다.",
            null
        );
    }
}
