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
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.prj.flashdeal.domain.file.service.FileService;
import com.prj.flashdeal.domain.product.dto.request.ProductCreateRequest;
import com.prj.flashdeal.domain.product.dto.request.ProductSearchCondForAdmin;
import com.prj.flashdeal.domain.product.dto.request.ProductUpdateRequest;
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
    private final FileService fileService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(
        @RequestPart("data") @Valid ProductCreateRequest request,
        @RequestPart(value = "image", required = false) MultipartFile image
    ) {
        String imageUrl = (image != null && !image.isEmpty()) ? fileService.uploadFile(image) : null;
        return ApiResponse.success(
            HttpStatus.CREATED,
            "상품 등록이 완료되었습니다.",
            productService.createProduct(request, imageUrl)
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

    @PatchMapping(value = "/{productId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(
        @PathVariable Long productId,
        @RequestPart("data") ProductUpdateRequest request,
        @RequestPart(value = "image", required = false) MultipartFile image
    ) {
        String imageUrl = (image != null && !image.isEmpty()) ? fileService.uploadFile(image) : null;
        return ApiResponse.success(
            HttpStatus.OK,
            "상품 수정이 완료되었습니다.",
            productService.updateProduct(productId, request, imageUrl)
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
