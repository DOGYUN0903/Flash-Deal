package com.prj.flashdeal.domain.product.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.prj.flashdeal.domain.product.exception.ProductException;

@DisplayName("Product 엔티티 단위 테스트")
class ProductTest {

    // ========== 빌더 기본값 ==========

    @Test
    @DisplayName("재고가 있으면 ON_SALE 상태로 생성됨")
    void builder_WithStock_StatusIsOnSale() {
        // when
        Product product = createProduct(100);

        // then
        assertThat(product.getStatus()).isEqualTo(ProductStatus.ON_SALE);
        assertThat(product.getStockQuantity()).isEqualTo(100);
    }

    @Test
    @DisplayName("재고가 0이면 PREPARING 상태로 생성됨")
    void builder_WithZeroStock_StatusIsPreparing() {
        // when
        Product product = createProduct(0);

        // then
        assertThat(product.getStatus()).isEqualTo(ProductStatus.PREPARING);
        assertThat(product.getStockQuantity()).isEqualTo(0);
    }

    @Test
    @DisplayName("재고를 null로 생성하면 stockQuantity=0, PREPARING 상태")
    void builder_WithNullStock_StockIsZeroAndPreparing() {
        // when
        Product product = Product.builder()
            .name("상품")
            .description("설명")
            .price(10000)
            .stock(null)
            .category(ProductCategory.ELECTRONICS)
            .build();

        // then
        assertThat(product.getStockQuantity()).isEqualTo(0);
        assertThat(product.getStatus()).isEqualTo(ProductStatus.PREPARING);
    }

    // ========== validateVisibleToUser ==========

    @Test
    @DisplayName("validateVisibleToUser 성공 - ON_SALE 상태")
    void validateVisibleToUser_Success_OnSale() {
        // given
        Product product = createProduct(10);

        // when & then
        assertThatCode(() -> product.validateVisibleToUser())
            .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("validateVisibleToUser 성공 - SOLD_OUT 상태")
    void validateVisibleToUser_Success_SoldOut() {
        // given
        Product product = createProduct(1);
        product.removeStock(1); // stockQuantity=0 → SOLD_OUT

        // when & then
        assertThatCode(() -> product.validateVisibleToUser())
            .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("validateVisibleToUser 실패 - PREPARING 상태")
    void validateVisibleToUser_Fail_Preparing() {
        // given
        Product product = createProduct(0); // status = PREPARING

        // when & then
        assertThatThrownBy(() -> product.validateVisibleToUser())
            .isInstanceOf(ProductException.class);
    }

    // ========== addStock ==========

    @Test
    @DisplayName("addStock 성공 - 재고 증가")
    void addStock_Success() {
        // given
        Product product = createProduct(10);

        // when
        product.addStock(5);

        // then
        assertThat(product.getStockQuantity()).isEqualTo(15);
    }

    @Test
    @DisplayName("addStock 성공 - SOLD_OUT 상태에서 ON_SALE로 복구")
    void addStock_Success_SoldOutToOnSale() {
        // given
        Product product = createProduct(1);
        product.removeStock(1); // SOLD_OUT
        assertThat(product.getStatus()).isEqualTo(ProductStatus.SOLD_OUT);

        // when
        product.addStock(10);

        // then
        assertThat(product.getStatus()).isEqualTo(ProductStatus.ON_SALE);
        assertThat(product.getStockQuantity()).isEqualTo(10);
    }

    @Test
    @DisplayName("addStock 성공 - PREPARING 상태에서 ON_SALE로 복구")
    void addStock_Success_PreparingToOnSale() {
        // given
        Product product = createProduct(0); // PREPARING

        // when
        product.addStock(10);

        // then
        assertThat(product.getStatus()).isEqualTo(ProductStatus.ON_SALE);
        assertThat(product.getStockQuantity()).isEqualTo(10);
    }

    @Test
    @DisplayName("addStock 실패 - 수량이 0이면 예외 발생")
    void addStock_Fail_ZeroQuantity() {
        // given
        Product product = createProduct(10);

        // when & then
        assertThatThrownBy(() -> product.addStock(0))
            .isInstanceOf(ProductException.class);
    }

    @Test
    @DisplayName("addStock 실패 - 수량이 음수이면 예외 발생")
    void addStock_Fail_NegativeQuantity() {
        // given
        Product product = createProduct(10);

        // when & then
        assertThatThrownBy(() -> product.addStock(-1))
            .isInstanceOf(ProductException.class);
    }

    // ========== removeStock ==========

    @Test
    @DisplayName("removeStock 성공 - 재고 감소")
    void removeStock_Success() {
        // given
        Product product = createProduct(10);

        // when
        product.removeStock(3);

        // then
        assertThat(product.getStockQuantity()).isEqualTo(7);
        assertThat(product.getStatus()).isEqualTo(ProductStatus.ON_SALE);
    }

    @Test
    @DisplayName("removeStock 성공 - 재고가 0이 되면 SOLD_OUT으로 변경")
    void removeStock_Success_StockZeroToSoldOut() {
        // given
        Product product = createProduct(5);

        // when
        product.removeStock(5);

        // then
        assertThat(product.getStockQuantity()).isEqualTo(0);
        assertThat(product.getStatus()).isEqualTo(ProductStatus.SOLD_OUT);
    }

    @Test
    @DisplayName("removeStock 실패 - 재고보다 많은 수량 차감 시 예외 발생")
    void removeStock_Fail_InsufficientStock() {
        // given
        Product product = createProduct(5);

        // when & then
        assertThatThrownBy(() -> product.removeStock(10))
            .isInstanceOf(ProductException.class);
    }

    @Test
    @DisplayName("removeStock 실패 - 수량이 0이면 예외 발생")
    void removeStock_Fail_ZeroQuantity() {
        // given
        Product product = createProduct(10);

        // when & then
        assertThatThrownBy(() -> product.removeStock(0))
            .isInstanceOf(ProductException.class);
    }

    @Test
    @DisplayName("removeStock 실패 - 수량이 음수이면 예외 발생")
    void removeStock_Fail_NegativeQuantity() {
        // given
        Product product = createProduct(10);

        // when & then
        assertThatThrownBy(() -> product.removeStock(-1))
            .isInstanceOf(ProductException.class);
    }

    // ========== updateStock ==========

    @Test
    @DisplayName("updateStock 성공 - 재고 양수이면 ON_SALE")
    void updateStock_Success_OnSale() {
        // given
        Product product = createProduct(10);

        // when
        product.updateStock(50);

        // then
        assertThat(product.getStockQuantity()).isEqualTo(50);
        assertThat(product.getStatus()).isEqualTo(ProductStatus.ON_SALE);
    }

    @Test
    @DisplayName("updateStock 성공 - 재고 0이면 SOLD_OUT")
    void updateStock_Success_SoldOut() {
        // given
        Product product = createProduct(10);

        // when
        product.updateStock(0);

        // then
        assertThat(product.getStockQuantity()).isEqualTo(0);
        assertThat(product.getStatus()).isEqualTo(ProductStatus.SOLD_OUT);
    }

    @Test
    @DisplayName("updateStock 실패 - 음수 재고이면 예외 발생")
    void updateStock_Fail_NegativeStock() {
        // given
        Product product = createProduct(10);

        // when & then
        assertThatThrownBy(() -> product.updateStock(-1))
            .isInstanceOf(ProductException.class);
    }

    // ========== updateInfo ==========

    @Test
    @DisplayName("updateInfo 성공 - 전달된 필드만 변경됨")
    void updateInfo_Success_PartialUpdate() {
        // given
        Product product = createProduct(10);

        // when
        product.updateInfo("새상품명", null, null, null, null);

        // then
        assertThat(product.getName()).isEqualTo("새상품명");
        assertThat(product.getDescription()).isEqualTo("상품 설명"); // 변경 없음
        assertThat(product.getPrice()).isEqualTo(10000);            // 변경 없음
    }

    @Test
    @DisplayName("updateInfo 성공 - 재고 수정 시 status도 함께 변경됨")
    void updateInfo_Success_StockAndStatusUpdated() {
        // given
        Product product = createProduct(10);

        // when
        product.updateInfo(null, null, null, 0, null);

        // then
        assertThat(product.getStockQuantity()).isEqualTo(0);
        assertThat(product.getStatus()).isEqualTo(ProductStatus.SOLD_OUT);
    }

    @Test
    @DisplayName("updateInfo 실패 - 이름이 공백이면 예외 발생")
    void updateInfo_Fail_BlankName() {
        // given
        Product product = createProduct(10);

        // when & then
        assertThatThrownBy(() -> product.updateInfo("   ", null, null, null, null))
            .isInstanceOf(ProductException.class);
    }

    @Test
    @DisplayName("updateInfo 실패 - 가격이 0이면 예외 발생")
    void updateInfo_Fail_ZeroPrice() {
        // given
        Product product = createProduct(10);

        // when & then
        assertThatThrownBy(() -> product.updateInfo(null, null, 0, null, null))
            .isInstanceOf(ProductException.class);
    }

    @Test
    @DisplayName("updateInfo 실패 - 가격이 음수이면 예외 발생")
    void updateInfo_Fail_NegativePrice() {
        // given
        Product product = createProduct(10);

        // when & then
        assertThatThrownBy(() -> product.updateInfo(null, null, -1000, null, null))
            .isInstanceOf(ProductException.class);
    }

    @Test
    @DisplayName("updateInfo 실패 - 재고가 음수이면 예외 발생")
    void updateInfo_Fail_NegativeStock() {
        // given
        Product product = createProduct(10);

        // when & then
        assertThatThrownBy(() -> product.updateInfo(null, null, null, -1, null))
            .isInstanceOf(ProductException.class);
    }

    // ========== delete ==========

    @Test
    @DisplayName("delete 성공 - 재고가 0이면 소프트 딜리트")
    void delete_Success_ZeroStock() {
        // given
        Product product = createProduct(0);

        // when
        product.delete();

        // then
        assertThat(product.getIsDeleted()).isTrue();
    }

    @Test
    @DisplayName("delete 실패 - 재고가 남아있으면 예외 발생")
    void delete_Fail_StockRemains() {
        // given
        Product product = createProduct(10);

        // when & then
        assertThatThrownBy(() -> product.delete())
            .isInstanceOf(ProductException.class);
    }

    // ========== 헬퍼 메서드 ==========

    private Product createProduct(Integer stock) {
        return Product.builder()
            .name("테스트 상품")
            .description("상품 설명")
            .price(10000)
            .stock(stock)
            .category(ProductCategory.ELECTRONICS)
            .build();
    }
}
