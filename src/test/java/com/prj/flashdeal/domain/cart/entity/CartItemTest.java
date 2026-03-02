package com.prj.flashdeal.domain.cart.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.prj.flashdeal.domain.cart.exception.CartException;
import com.prj.flashdeal.domain.member.entity.Member;
import com.prj.flashdeal.domain.product.entity.Product;
import com.prj.flashdeal.domain.product.entity.ProductCategory;

@DisplayName("CartItem 엔티티 단위 테스트")
class CartItemTest {

    // ========== addQuantity ==========

    @Test
    @DisplayName("addQuantity 성공 - 수량이 증가됨")
    void addQuantity_Success() {
        // given
        CartItem cartItem = createCartItem(2);

        // when
        cartItem.addQuantity(3);

        // then
        assertThat(cartItem.getQuantity()).isEqualTo(5);
    }

    @Test
    @DisplayName("addQuantity 실패 - 수량이 0이면 예외 발생")
    void addQuantity_Fail_Zero() {
        // given
        CartItem cartItem = createCartItem(2);

        // when & then
        assertThatThrownBy(() -> cartItem.addQuantity(0))
            .isInstanceOf(CartException.class);
    }

    @Test
    @DisplayName("addQuantity 실패 - 수량이 음수이면 예외 발생")
    void addQuantity_Fail_Negative() {
        // given
        CartItem cartItem = createCartItem(2);

        // when & then
        assertThatThrownBy(() -> cartItem.addQuantity(-1))
            .isInstanceOf(CartException.class);
    }

    // ========== updateQuantity ==========

    @Test
    @DisplayName("updateQuantity 성공 - 수량이 변경됨")
    void updateQuantity_Success() {
        // given
        CartItem cartItem = createCartItem(2);

        // when
        cartItem.updateQuantity(10);

        // then
        assertThat(cartItem.getQuantity()).isEqualTo(10);
    }

    @Test
    @DisplayName("updateQuantity 실패 - 수량이 0이면 예외 발생")
    void updateQuantity_Fail_Zero() {
        // given
        CartItem cartItem = createCartItem(2);

        // when & then
        assertThatThrownBy(() -> cartItem.updateQuantity(0))
            .isInstanceOf(CartException.class);
    }

    @Test
    @DisplayName("updateQuantity 실패 - 수량이 음수이면 예외 발생")
    void updateQuantity_Fail_Negative() {
        // given
        CartItem cartItem = createCartItem(2);

        // when & then
        assertThatThrownBy(() -> cartItem.updateQuantity(-1))
            .isInstanceOf(CartException.class);
    }

    // ========== calculateItemTotalPrice ==========

    @Test
    @DisplayName("calculateItemTotalPrice - 상품 현재 가격 x 수량이 반환됨")
    void calculateItemTotalPrice_Success() {
        // given
        CartItem cartItem = createCartItem(3); // 상품 가격 10000, 수량 3

        // when
        int totalPrice = cartItem.calculateItemTotalPrice();

        // then
        assertThat(totalPrice).isEqualTo(30000);
    }

    // ========== 헬퍼 메서드 ==========

    private CartItem createCartItem(int quantity) {
        Member member = Member.builder()
            .email("test@test.com")
            .password("pw")
            .name("테스터")
            .phoneNumber("010-1234-5678")
            .build();

        Product product = Product.builder()
            .name("테스트 상품")
            .description("설명")
            .price(10000)
            .stock(100)
            .category(ProductCategory.ELECTRONICS)
            .build();

        return CartItem.builder()
            .member(member)
            .product(product)
            .quantity(quantity)
            .build();
    }
}
