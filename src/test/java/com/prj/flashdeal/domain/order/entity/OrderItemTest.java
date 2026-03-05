package com.prj.flashdeal.domain.order.entity;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.prj.flashdeal.domain.product.entity.Product;
import com.prj.flashdeal.domain.product.entity.ProductCategory;

@DisplayName("OrderItem 엔티티 단위 테스트")
class OrderItemTest {

    // ========== createOrderItem ==========

    @Test
    @DisplayName("createOrderItem - 상품 주문 시점 가격과 수량으로 생성됨")
    void createOrderItem_Success() {
        // given
        Product product = createProduct(15000);
        int quantity = 3;

        // when
        OrderItem orderItem = OrderItem.createOrderItem(product, quantity);

        // then
        assertThat(orderItem.getPrice()).isEqualTo(15000);
        assertThat(orderItem.getQuantity()).isEqualTo(3);
        assertThat(orderItem.getOrderPrice()).isEqualTo(45000);
    }

    // ========== calculateTotalPrice ==========

    @Test
    @DisplayName("calculateTotalPrice - 단가 x 수량이 반환됨")
    void calculateTotalPrice_Success() {
        // given
        OrderItem orderItem = OrderItem.createOrderItem(createProduct(10000), 4);

        // when
        int totalPrice = orderItem.calculateTotalPrice();

        // then
        assertThat(totalPrice).isEqualTo(40000);
    }

    // ========== 헬퍼 메서드 ==========

    private Product createProduct(int price) {
        Product product = Product.builder()
            .name("테스트 상품")
            .description("설명")
            .price(price)
            .category(ProductCategory.ELECTRONICS)
            .build();
        product.markOnSale();
        return product;
    }
}
