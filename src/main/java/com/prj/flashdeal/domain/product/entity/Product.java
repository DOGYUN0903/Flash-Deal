package com.prj.flashdeal.domain.product.entity;

import com.prj.flashdeal.domain.product.exception.ProductErrorCode;
import com.prj.flashdeal.domain.product.exception.ProductException;
import com.prj.flashdeal.global.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "products")
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long id;

    @Column(nullable = false)
    private String name; // 상품 이름

    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String description; // 상품 설명

    @Column(nullable = false)
    private Integer price;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductStatus status; // 상품 상태(판매 준비중, 판매중, 품절)

    @Column
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductCategory category;

    @Builder
    private Product(String name, String description, Integer price, ProductCategory category, String imageUrl) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.status = ProductStatus.PREPARING; // 재고는 Stock 도메인에서 관리
        this.category = category;
        this.imageUrl = imageUrl;
    }

    public void validateVisibleToUser() {
        if (this.status != ProductStatus.ON_SALE && this.status != ProductStatus.SOLD_OUT) {
            throw new ProductException(ProductErrorCode.PRODUCT_NOT_FOUND);
        }
    }

    /**
     * 재고가 0이 되었을 때 호출 — StockService에서 호출
     */
    public void markSoldOut() {
        this.status = ProductStatus.SOLD_OUT;
    }

    /**
     * 재고가 다시 생겼을 때 호출 — StockService에서 호출
     */
    public void markOnSale() {
        if (this.status != ProductStatus.ON_SALE) {
            this.status = ProductStatus.ON_SALE;
        }
    }

    public void updateInfo(String name, String description, Integer price, String imageUrl) {
        if (name != null) {
            if (name.isBlank()) {
                throw new ProductException(ProductErrorCode.BLANK_PRODUCT_NAME);
            }
            this.name = name;
        }
        if (description != null) {
            this.description = description;
        }
        if (price != null) {
            if (price <= 0) {
                throw new ProductException(ProductErrorCode.INVALID_PRICE);
            }
            this.price = price;
        }
        if (imageUrl != null) {
            this.imageUrl = imageUrl;
        }
    }
}
