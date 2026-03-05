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
    @DisplayName("상품 생성 시 기본 상태는 PREPARING")
    void builder_DefaultStatus_IsPreparing() {
        // when
        Product product = createProduct();

        // then
        assertThat(product.getStatus()).isEqualTo(ProductStatus.PREPARING);
    }

    // ========== validateVisibleToUser ==========

    @Test
    @DisplayName("validateVisibleToUser 성공 - ON_SALE 상태")
    void validateVisibleToUser_Success_OnSale() {
        // given
        Product product = createProduct();
        product.markOnSale();

        // when & then
        assertThatCode(() -> product.validateVisibleToUser())
            .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("validateVisibleToUser 성공 - SOLD_OUT 상태")
    void validateVisibleToUser_Success_SoldOut() {
        // given
        Product product = createProduct();
        product.markSoldOut();

        // when & then
        assertThatCode(() -> product.validateVisibleToUser())
            .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("validateVisibleToUser 실패 - PREPARING 상태")
    void validateVisibleToUser_Fail_Preparing() {
        // given
        Product product = createProduct(); // status = PREPARING

        // when & then
        assertThatThrownBy(() -> product.validateVisibleToUser())
            .isInstanceOf(ProductException.class);
    }

    // ========== markSoldOut / markOnSale ==========

    @Test
    @DisplayName("markSoldOut 성공 - SOLD_OUT 상태로 변경")
    void markSoldOut_Success() {
        // given
        Product product = createProduct();
        product.markOnSale();
        assertThat(product.getStatus()).isEqualTo(ProductStatus.ON_SALE);

        // when
        product.markSoldOut();

        // then
        assertThat(product.getStatus()).isEqualTo(ProductStatus.SOLD_OUT);
    }

    @Test
    @DisplayName("markOnSale 성공 - ON_SALE 상태로 변경")
    void markOnSale_Success() {
        // given
        Product product = createProduct();
        product.markSoldOut();
        assertThat(product.getStatus()).isEqualTo(ProductStatus.SOLD_OUT);

        // when
        product.markOnSale();

        // then
        assertThat(product.getStatus()).isEqualTo(ProductStatus.ON_SALE);
    }

    @Test
    @DisplayName("markOnSale 멱등성 - 이미 ON_SALE이면 상태 유지")
    void markOnSale_Idempotent() {
        // given
        Product product = createProduct();
        product.markOnSale();

        // when
        product.markOnSale(); // 중복 호출

        // then
        assertThat(product.getStatus()).isEqualTo(ProductStatus.ON_SALE);
    }

    // ========== updateInfo ==========

    @Test
    @DisplayName("updateInfo 성공 - 전달된 필드만 변경됨")
    void updateInfo_Success_PartialUpdate() {
        // given
        Product product = createProduct();

        // when
        product.updateInfo("새상품명", null, null, null);

        // then
        assertThat(product.getName()).isEqualTo("새상품명");
        assertThat(product.getDescription()).isEqualTo("상품 설명"); // 변경 없음
        assertThat(product.getPrice()).isEqualTo(10000);            // 변경 없음
    }

    @Test
    @DisplayName("updateInfo 실패 - 이름이 공백이면 예외 발생")
    void updateInfo_Fail_BlankName() {
        // given
        Product product = createProduct();

        // when & then
        assertThatThrownBy(() -> product.updateInfo("   ", null, null, null))
            .isInstanceOf(ProductException.class);
    }

    @Test
    @DisplayName("updateInfo 실패 - 가격이 0이면 예외 발생")
    void updateInfo_Fail_ZeroPrice() {
        // given
        Product product = createProduct();

        // when & then
        assertThatThrownBy(() -> product.updateInfo(null, null, 0, null))
            .isInstanceOf(ProductException.class);
    }

    @Test
    @DisplayName("updateInfo 실패 - 가격이 음수이면 예외 발생")
    void updateInfo_Fail_NegativePrice() {
        // given
        Product product = createProduct();

        // when & then
        assertThatThrownBy(() -> product.updateInfo(null, null, -1000, null))
            .isInstanceOf(ProductException.class);
    }

    // ========== delete ==========

    @Test
    @DisplayName("delete 성공 - 소프트 딜리트")
    void delete_Success() {
        // given
        Product product = createProduct();

        // when
        product.delete();

        // then
        assertThat(product.getIsDeleted()).isTrue();
    }

    // ========== 헬퍼 메서드 ==========

    private Product createProduct() {
        return Product.builder()
            .name("테스트 상품")
            .description("상품 설명")
            .price(10000)
            .category(ProductCategory.ELECTRONICS)
            .build();
    }
}
