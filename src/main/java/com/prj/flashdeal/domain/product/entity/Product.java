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
@Table(name = "product")
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long id;

    @Column(nullable = false)
    private String name; // 상품 이름

    @Column(nullable = false)
    private String description; // 상품 설명

    @Column(nullable = false)
    private Integer price;

    @Column(nullable = false)
    private Integer stockQuantity; // 상품 재고

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductStatus status; // 상품 상태(판매 준비중, 판매중, 품절)

    @Override
    public void delete() {
        if (this.stockQuantity > 0) {
            throw new ProductException(ProductErrorCode.STOCK_REMAINS);
        }
        super.delete();
    }

    @Builder
    private Product(String name, String description, Integer price) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.stockQuantity = 0;
        this.status = ProductStatus.PREPARING;
    }

    public void addStock(Integer quantity) {
        if (quantity <= 0) {
            throw new ProductException(ProductErrorCode.INVALID_STOCK_QUANTITY);
        }
        this.stockQuantity += quantity;

        if (this.status == ProductStatus.PREPARING || this.status == ProductStatus.SOLD_OUT) {
            this.status = ProductStatus.ON_SALE;
        }
    }

    public void updateInfo(String name, String description, Integer price) {
        if (name != null){
            if (name.isBlank()) {
                throw new ProductException(ProductErrorCode.BLANK_PRODUCT_NAME);
            }
            this.name = name;
        }
        if (description != null){
            this.description = description;
        }
        if (price != null) {
            if (price <= 0) {
                throw new ProductException(ProductErrorCode.INVALID_PRICE);
            }
            this.price = price;
        }
    }
}
