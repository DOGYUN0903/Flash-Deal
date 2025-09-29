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
}
