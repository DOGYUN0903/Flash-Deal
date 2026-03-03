package com.prj.flashdeal.domain.product.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.prj.flashdeal.domain.product.dto.request.ProductCreateRequest;
import com.prj.flashdeal.domain.product.dto.request.ProductSearchCondForAdmin;
import com.prj.flashdeal.domain.product.dto.request.ProductUpdateRequest;
import com.prj.flashdeal.domain.product.dto.response.ProductResponse;
import com.prj.flashdeal.domain.product.dto.response.ProductSummaryResponse;
import com.prj.flashdeal.domain.product.service.ProductService;
import com.prj.flashdeal.global.response.ApiResponse;
import com.prj.flashdeal.global.response.PageResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Admin - Product", description = "상품 관리 API (어드민)")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/products")
public class AdminProductController {

    private final ProductService productService;

    @Operation(summary = "상품 등록", description = "새 상품을 등록합니다. 이미지 파일을 함께 업로드할 수 있습니다.")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(
        @ModelAttribute @Valid ProductCreateRequest request
    ) {
        return ApiResponse.success(
            HttpStatus.CREATED,
            "상품 등록이 완료되었습니다.",
            productService.createProduct(request)
        );
    }

    @Operation(summary = "상품 목록 검색 (어드민)", description = "전체 상품을 상태, 카테고리 등 조건으로 검색합니다. (페이징)")
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

    @Operation(summary = "상품 상세 조회 (어드민)", description = "삭제된 상품을 포함한 상품 상세 정보를 조회합니다.")
    @GetMapping("/{productId}")
    public ResponseEntity<ApiResponse<ProductResponse>> getProductForAdmin(
        @PathVariable Long productId
    ) {
        return ApiResponse.success(
            HttpStatus.OK,
            "관리자용 상품 상세 조회에 성공하였습니다.",
            productService.getProductForAdmin(productId)
        );
    }

    @Operation(summary = "상품 수정", description = "상품 정보를 수정합니다. 이미지 파일을 교체할 수 있습니다.")
    @PatchMapping(value = "/{productId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(
        @PathVariable Long productId,
        @ModelAttribute ProductUpdateRequest request
    ) {
        return ApiResponse.success(
            HttpStatus.OK,
            "상품 수정이 완료되었습니다.",
            productService.updateProduct(productId, request)
        );
    }

    @Operation(summary = "상품 삭제", description = "상품을 소프트 딜리트 처리합니다.")
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
