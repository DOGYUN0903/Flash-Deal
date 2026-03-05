package com.prj.flashdeal.domain.stock.entity;

import com.prj.flashdeal.domain.product.entity.Product;
import com.prj.flashdeal.domain.stock.exception.StockErrorCode;
import com.prj.flashdeal.domain.stock.exception.StockException;
import com.prj.flashdeal.global.entity.BaseTimeEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "stocks")
public class Stock extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "stock_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false, unique = true)
    private Product product;

    @Column(nullable = false)
    private int quantity;

    @Builder
    private Stock(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    public void decrease(int amount) {
        if (amount <= 0) {
            throw new StockException(StockErrorCode.INVALID_STOCK_QUANTITY);
        }
        if (this.quantity < amount) {
            throw new StockException(StockErrorCode.OUT_OF_STOCK);
        }
        this.quantity -= amount;
    }

    public void increase(int amount) {
        if (amount <= 0) {
            throw new StockException(StockErrorCode.INVALID_STOCK_QUANTITY);
        }
        this.quantity += amount;
    }

}
