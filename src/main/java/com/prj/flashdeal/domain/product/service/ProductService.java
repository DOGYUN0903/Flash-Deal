package com.prj.flashdeal.domain.product.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prj.flashdeal.domain.product.dto.request.ProductCreateRequest;
import com.prj.flashdeal.domain.product.dto.request.StockAddRequest;
import com.prj.flashdeal.domain.product.dto.response.ProductResponse;
import com.prj.flashdeal.domain.product.entity.Product;
import com.prj.flashdeal.domain.product.exception.ProductErrorCode;
import com.prj.flashdeal.domain.product.exception.ProductException;
import com.prj.flashdeal.domain.product.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

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
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new ProductException(ProductErrorCode.PRODUCT_NOT_FOUND));

        product.addStock(request.getQuantity());

        return ProductResponse.from(product);
    }
}
