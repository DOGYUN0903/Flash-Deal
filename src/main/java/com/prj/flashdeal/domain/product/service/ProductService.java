package com.prj.flashdeal.domain.product.service;

import com.prj.flashdeal.domain.file.service.FileService;
import com.prj.flashdeal.domain.product.entity.ProductStatus;
import com.prj.flashdeal.domain.product.exception.ProductErrorCode;
import com.prj.flashdeal.domain.product.exception.ProductException;
import com.prj.flashdeal.domain.stock.metrics.StockMetrics;
import com.prj.flashdeal.domain.stock.service.StockService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.prj.flashdeal.domain.product.dto.request.ProductCreateRequest;
import com.prj.flashdeal.domain.product.dto.request.ProductSearchCondForAdmin;
import com.prj.flashdeal.domain.product.dto.request.ProductSearchCondForUser;
import com.prj.flashdeal.domain.product.dto.request.ProductUpdateRequest;
import com.prj.flashdeal.domain.product.dto.response.ProductResponse;
import com.prj.flashdeal.domain.product.dto.response.ProductResponseForUser;
import com.prj.flashdeal.domain.product.dto.response.ProductSummaryResponse;
import com.prj.flashdeal.domain.product.entity.Product;
import com.prj.flashdeal.domain.product.repository.ProductRepository;
import com.prj.flashdeal.global.response.PageResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final FileService fileService;
    private final StockMetrics stockMetrics;
    private final StockService stockService;

    @Value("${ncp.object-storage.default-image-url}")
    private String defaultImageUrl;

    // ---------------- Admin 전용 비즈니스 로직 ----------------
    @Transactional
    public ProductResponse createProduct(ProductCreateRequest request) {
        String imageUrl = uploadImageOrDefault(request.getImage());

        Product product = Product.builder()
            .name(request.getName())
            .description(request.getDescription())
            .price(request.getPrice())
            .category(request.getCategory())
            .imageUrl(imageUrl)
            .build();

        Product savedProduct = productRepository.save(product);

        int stock = request.getStock() != null ? request.getStock() : 0;
        stockService.createStock(savedProduct, stock);

        if (stock > 0) {
            savedProduct.markOnSale();
        }

        // 신규 상품 재고 Gauge 등록 — @PostConstruct로 기존 상품은 이미 등록됨
        stockMetrics.registerStockGauge(savedProduct.getId());

        return ProductResponse.from(savedProduct, stock);
    }

    @Transactional(readOnly = true)
    public PageResponse<ProductSummaryResponse> searchProductsForAdmin(ProductSearchCondForAdmin cond, Pageable pageable) {
        return new PageResponse<>(productRepository.searchProductsForAdmin(cond, pageable));
    }

    @Transactional(readOnly = true)
    public ProductResponse getProductForAdmin(Long productId) {
        Product product = getProduct(productId);
        int stock = stockService.getStock(productId);

        return ProductResponse.from(product, stock);
    }

    @Transactional
    public ProductResponse updateProduct(Long productId, ProductUpdateRequest request) {
        Product product = getProduct(productId);
        MultipartFile image = request.getImage();
        String imageUrl = (image != null && !image.isEmpty()) ? fileService.uploadFile(image) : null;

        product.updateInfo(
            request.getName(),
            request.getDescription(),
            request.getPrice(),
            imageUrl
        );

        if (request.getStock() != null) {
            stockService.updateStock(productId, request.getStock());
        }

        int currentStock = stockService.getStock(productId);
        return ProductResponse.from(product, currentStock);
    }

    @Transactional
    public void deleteProduct(Long productId) {
        Product product = getProduct(productId);

        int stock = stockService.getStock(productId);
        if (stock > 0) {
            throw new ProductException(ProductErrorCode.STOCK_REMAINS);
        }

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

        product.validateVisibleToUser();

        int stock = stockService.getStock(productId);
        return ProductResponseForUser.from(product, stock);
    }

    // ---------------- 다른 도메인과 통신을 위한 메서드 ----------------
    @Transactional(readOnly = true)
    public Product getProductEntity(Long productId) {
        return getProduct(productId);
    }

    /**
     * 주문 가능한 상품 조회 — ON_SALE 상태 검증만 수행.
     * 재고 차감은 StockService.decreaseStock()으로 별도 호출할 것.
     */
    @Transactional
    public Product findCartableProduct(Long productId) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new ProductException(ProductErrorCode.PRODUCT_NOT_FOUND));

        if (product.getIsDeleted()) {
            throw new ProductException(ProductErrorCode.ALREADY_DELETED_PRODUCT);
        }
        if (product.getStatus() != ProductStatus.ON_SALE) {
            throw new ProductException(ProductErrorCode.PRODUCT_NOT_ON_SALE);
        }

        return product;
    }

    // ---------------- private 헬퍼 메서드 ----------------
    private String uploadImageOrDefault(MultipartFile image) {
        return (image != null && !image.isEmpty()) ? fileService.uploadFile(image) : defaultImageUrl;
    }

    private Product getProduct(Long productId) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new ProductException(ProductErrorCode.PRODUCT_NOT_FOUND));

        if (product.getIsDeleted()) {
            throw new ProductException(ProductErrorCode.ALREADY_DELETED_PRODUCT);
        }

        return product;
    }
}
