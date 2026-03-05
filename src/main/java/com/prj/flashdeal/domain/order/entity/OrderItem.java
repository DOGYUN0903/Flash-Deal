package com.prj.flashdeal.domain.order.entity;

import com.prj.flashdeal.domain.product.entity.Product;
import com.prj.flashdeal.global.entity.BaseTimeEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "order_items")
public class OrderItem extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private int price; // 주문 시점의 상품 단가

    @Column(nullable = false)
    private int orderPrice; // 총 주문 금액 (price * quantity)

    @Builder
    private OrderItem(Order order, Product product, int quantity, int price) {
        this.order = order;
        this.product = product;
        this.quantity = quantity;
        this.price = price;
        this.orderPrice = price * quantity;
    }

    /**
     * 주문 항목 생성 정적 팩토리 메서드
     */
    public static OrderItem createOrderItem(Product product, int quantity) {
        return OrderItem.builder()
                .product(product)
                .quantity(quantity)
                .price(product.getPrice())
                .build();
    }

    public static OrderItem createOrderItem(Product product, int quantity, int price) {
        return OrderItem.builder()
                .product(product)
                .quantity(quantity)
                .price(price)
                .build();
    }

    /**
     * 주문과의 연관관계 설정 (Order.addOrderItem()에서만 호출)
     */
    protected void setOrder(Order order) {
        this.order = order;
    }

    /**
     * 총 주문 금액 계산
     */
    public int calculateTotalPrice() {
        return this.price * this.quantity;
    }

    /**
     * 상품 ID 반환 (재고 복구용)
     */
    public Long getProductId() {
        return this.product.getId();
    }
}
