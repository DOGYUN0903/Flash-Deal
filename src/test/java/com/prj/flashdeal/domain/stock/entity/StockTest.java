package com.prj.flashdeal.domain.stock.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.prj.flashdeal.domain.product.entity.Product;
import com.prj.flashdeal.domain.product.entity.ProductCategory;
import com.prj.flashdeal.domain.stock.exception.StockException;

@DisplayName("Stock 엔티티 단위 테스트")
class StockTest {

    // ========== decrease ==========

    @Test
    @DisplayName("decrease 성공 - 재고 차감")
    void decrease_Success() {
        // given
        Stock stock = createStock(10);

        // when
        stock.decrease(3);

        // then
        assertThat(stock.getQuantity()).isEqualTo(7);
    }

    @Test
    @DisplayName("decrease 성공 - 재고가 정확히 0이 됨")
    void decrease_Success_QuantityBecomesZero() {
        // given
        Stock stock = createStock(5);

        // when
        stock.decrease(5);

        // then
        assertThat(stock.getQuantity()).isEqualTo(0);
    }

    @Test
    @DisplayName("decrease 실패 - 재고 부족")
    void decrease_Fail_OutOfStock() {
        // given
        Stock stock = createStock(3);

        // when & then
        assertThatThrownBy(() -> stock.decrease(5))
            .isInstanceOf(StockException.class);
    }

    @Test
    @DisplayName("decrease 실패 - 수량이 0이면 예외 발생")
    void decrease_Fail_ZeroAmount() {
        // given
        Stock stock = createStock(10);

        // when & then
        assertThatThrownBy(() -> stock.decrease(0))
            .isInstanceOf(StockException.class);
    }

    @Test
    @DisplayName("decrease 실패 - 수량이 음수이면 예외 발생")
    void decrease_Fail_NegativeAmount() {
        // given
        Stock stock = createStock(10);

        // when & then
        assertThatThrownBy(() -> stock.decrease(-1))
            .isInstanceOf(StockException.class);
    }

    // ========== increase ==========

    @Test
    @DisplayName("increase 성공 - 재고 증가")
    void increase_Success() {
        // given
        Stock stock = createStock(5);

        // when
        stock.increase(10);

        // then
        assertThat(stock.getQuantity()).isEqualTo(15);
    }

    @Test
    @DisplayName("increase 실패 - 수량이 0이면 예외 발생")
    void increase_Fail_ZeroAmount() {
        // given
        Stock stock = createStock(5);

        // when & then
        assertThatThrownBy(() -> stock.increase(0))
            .isInstanceOf(StockException.class);
    }

    @Test
    @DisplayName("increase 실패 - 수량이 음수이면 예외 발생")
    void increase_Fail_NegativeAmount() {
        // given
        Stock stock = createStock(5);

        // when & then
        assertThatThrownBy(() -> stock.increase(-1))
            .isInstanceOf(StockException.class);
    }

    // ========== 헬퍼 메서드 ==========

    private Stock createStock(int quantity) {
        Product product = Product.builder()
            .name("테스트 상품")
            .description("설명")
            .price(10000)
            .category(ProductCategory.ELECTRONICS)
            .build();
        return Stock.builder()
            .product(product)
            .quantity(quantity)
            .build();
    }
}
