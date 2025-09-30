package com.prj.flashdeal.domain.product.service;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prj.flashdeal.domain.product.dto.request.ProductCreateRequest;
import com.prj.flashdeal.domain.product.dto.request.ProductSearchCondForAdmin;
import com.prj.flashdeal.domain.product.dto.request.ProductSearchCondForUser;
import com.prj.flashdeal.domain.product.dto.request.ProductUpdateRequest;
import com.prj.flashdeal.domain.product.dto.request.StockAddRequest;
import com.prj.flashdeal.domain.product.dto.response.ProductResponse;
import com.prj.flashdeal.domain.product.dto.response.ProductResponseForUser;
import com.prj.flashdeal.domain.product.dto.response.ProductSummaryResponse;
import com.prj.flashdeal.domain.product.entity.Product;
import com.prj.flashdeal.domain.product.entity.ProductStatus;
import com.prj.flashdeal.domain.product.exception.ProductErrorCode;
import com.prj.flashdeal.domain.product.exception.ProductException;
import com.prj.flashdeal.domain.product.repository.ProductRepository;
import com.prj.flashdeal.global.response.PageResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    // ---------------- Admin 전용 비즈니스 로직 ----------------
    @Transactional
    public ProductResponse createProduct(ProductCreateRequest request) {
        Product product = Product.builder()
            .name(request.getName())
            .description(request.getDescription())
            .price(request.getPrice())
            .build();

        Product savedProduct = productRepository.save(product);

        return ProductResponse.from(savedProduct);
    }

    @Transactional
    public ProductResponse addStock(Long productId, StockAddRequest request) {
        Product product = getProduct(productId);

        product.addStock(request.getQuantity());

        return ProductResponse.from(product);
    }

    @Transactional(readOnly = true)
    public PageResponse<ProductSummaryResponse> searchProductsForAdmin(ProductSearchCondForAdmin cond, Pageable pageable) {
        return new PageResponse<>(productRepository.searchProductsForAdmin(cond, pageable));
    }

    @Transactional(readOnly = true)
    public ProductResponse getProductForAdmin(Long productId) {
        Product product = getProduct(productId);

        return ProductResponse.from(product);
    }

    @Transactional
    public ProductResponse updateProduct(Long productId, ProductUpdateRequest request) {
        Product product = getProduct(productId);

        product.updateInfo(
            request.getName(),
            request.getDescription(),
            request.getPrice()
        );

        return ProductResponse.from(product);
    }

    @Transactional
    public void deleteProduct(Long productId) {
        Product product = getProduct(productId);

        product.delete();
    }

    // ---------------- User 전용 비즈니스 로직 ----------------
    @Transactional(readOnly = true)
    public PageResponse<ProductSummaryResponse> searchProductsForUser(ProductSearchCondForUser cond, Pageable pageable) {
        return new PageResponse<>(productRepository.searchProductsForUser(cond, pageable));
    }

    @Transactional(readOnly = true)
    public ProductResponseForUser getProductForUser(Long productId) {
        Product product = getProduct(productId);

        if (product.getStatus() != ProductStatus.ON_SALE && product.getStatus() != ProductStatus.SOLD_OUT) {
            throw new ProductException(ProductErrorCode.PRODUCT_NOT_FOUND);
        }

        return ProductResponseForUser.from(product);
    }

    // ---------------- private 헬퍼 메서드 ----------------
    private Product getProduct(Long productId) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new ProductException(ProductErrorCode.PRODUCT_NOT_FOUND));

        if (product.getIsDeleted()) {
            throw new ProductException(ProductErrorCode.ALREADY_DELETED_PRODUCT);
        }

        return product;
    }
}
