package com.prj.flashdeal.domain.product.service;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prj.flashdeal.domain.product.dto.request.ProductCreateRequest;
import com.prj.flashdeal.domain.product.dto.request.ProductSearchCondForAdmin;
import com.prj.flashdeal.domain.product.dto.request.ProductSearchCondForUser;
import com.prj.flashdeal.domain.product.dto.request.ProductUpdateRequest;
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
    public ProductResponse createProduct(ProductCreateRequest request, String imageUrl) {
        Product product = Product.builder()
            .name(request.getName())
            .description(request.getDescription())
            .price(request.getPrice())
            .stock(request.getStock())
            .build();

        if (imageUrl != null) {
            product.updateImageUrl(imageUrl);
        }

        return ProductResponse.from(productRepository.save(product));
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
    public ProductResponse updateProduct(Long productId, ProductUpdateRequest request, String imageUrl) {
        Product product = getProduct(productId);

        product.updateInfo(
            request.getName(),
            request.getDescription(),
            request.getPrice()
        );

        if (request.getStock() != null) {
            product.updateStock(request.getStock());
        }

        if (imageUrl != null) {
            product.updateImageUrl(imageUrl);
        }

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

    // ---------------- 다른 도메인과 통신을 위한 메서드 ----------------
    @Transactional(readOnly = true)
    public Product getProductEntity(Long productId) {
        return getProduct(productId);
    }

    @Transactional(readOnly = true)
    public Product findCartableProduct(Long productId) {
        Product product = getProduct(productId);

        if (product.getStatus() != ProductStatus.ON_SALE) {
            throw new ProductException(ProductErrorCode.PRODUCT_NOT_ON_SALE);
        }

        return product;
    }

    /**
     * 재고 감소 (주문 생성용)
     */
    @Transactional
    public void decreaseStock(Long productId, int quantity) {
        Product product = getProduct(productId);
        product.removeStock(quantity);
    }

    /**
     * 재고 복구 (주문 취소용)
     */
    @Transactional
    public void increaseStock(Long productId, int quantity) {
        Product product = getProduct(productId);
        product.addStock(quantity);
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
