package com.prj.flashdeal.domain.product.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.verify;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import com.prj.flashdeal.domain.file.service.FileService;
import com.prj.flashdeal.domain.product.dto.request.ProductCreateRequest;
import com.prj.flashdeal.domain.product.dto.request.ProductUpdateRequest;
import com.prj.flashdeal.domain.product.dto.response.ProductResponse;
import com.prj.flashdeal.domain.product.dto.response.ProductResponseForUser;
import com.prj.flashdeal.domain.product.entity.Product;
import com.prj.flashdeal.domain.product.entity.ProductCategory;
import com.prj.flashdeal.domain.product.exception.ProductException;
import com.prj.flashdeal.domain.product.repository.ProductRepository;
import com.prj.flashdeal.domain.stock.metrics.StockMetrics;
import com.prj.flashdeal.domain.stock.service.StockService;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductService 단위 테스트")
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private FileService fileService;

    @Mock
    private StockMetrics stockMetrics;

    @Mock
    private StockService stockService;

    @InjectMocks
    private ProductService productService;

    @BeforeEach
    void setUp() {
        // @Value 필드는 Spring 컨텍스트 없이 주입되지 않으므로 직접 설정
        ReflectionTestUtils.setField(productService, "defaultImageUrl", "http://default.jpg");
    }

    // ========== createProduct ==========

    @Test
    @DisplayName("createProduct 성공 - 이미지 없으면 기본 이미지 사용")
    void createProduct_Success_DefaultImage() {
        // given
        ProductCreateRequest request = createProductCreateRequest(null);
        given(productRepository.save(any(Product.class))).willAnswer(inv -> {
            Product p = inv.getArgument(0);
            ReflectionTestUtils.setField(p, "id", 1L);
            return p;
        });

        // when
        ProductResponse response = productService.createProduct(request);

        // then
        assertThat(response).isNotNull();
        verify(fileService, never()).uploadFile(any());
    }

    @Test
    @DisplayName("createProduct 성공 - 이미지 있으면 FileService 호출")
    void createProduct_Success_UploadedImage() {
        // given
        MockMultipartFile image = new MockMultipartFile("image", "test.jpg", "image/jpeg", new byte[]{1, 2, 3});
        ProductCreateRequest request = createProductCreateRequest(image);

        given(fileService.uploadFile(image)).willReturn("http://uploaded.jpg");
        given(productRepository.save(any(Product.class))).willAnswer(inv -> {
            Product p = inv.getArgument(0);
            ReflectionTestUtils.setField(p, "id", 1L);
            return p;
        });

        // when
        ProductResponse response = productService.createProduct(request);

        // then
        assertThat(response).isNotNull();
        verify(fileService, times(1)).uploadFile(image);
    }

    // ========== getProductForAdmin ==========

    @Test
    @DisplayName("getProductForAdmin 성공")
    void getProductForAdmin_Success() {
        // given
        Long productId = 1L;
        Product product = createProduct(true);
        given(productRepository.findById(productId)).willReturn(Optional.of(product));

        // when
        ProductResponse response = productService.getProductForAdmin(productId);

        // then
        assertThat(response).isNotNull();
    }

    @Test
    @DisplayName("getProductForAdmin 실패 - 존재하지 않는 상품")
    void getProductForAdmin_Fail_NotFound() {
        // given
        given(productRepository.findById(anyLong())).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> productService.getProductForAdmin(1L))
            .isInstanceOf(ProductException.class);
    }

    @Test
    @DisplayName("getProductForAdmin 실패 - 삭제된 상품")
    void getProductForAdmin_Fail_Deleted() {
        // given
        Product product = createProduct(false);
        product.delete();
        given(productRepository.findById(1L)).willReturn(Optional.of(product));

        // when & then
        assertThatThrownBy(() -> productService.getProductForAdmin(1L))
            .isInstanceOf(ProductException.class);
    }

    // ========== updateProduct ==========

    @Test
    @DisplayName("updateProduct 성공 - 이미지 없으면 FileService 호출 안 함")
    void updateProduct_Success_NoImage() {
        // given
        Long productId = 1L;
        Product product = createProduct(true);
        ProductUpdateRequest request = createProductUpdateRequest(null);

        given(productRepository.findById(productId)).willReturn(Optional.of(product));

        // when
        ProductResponse response = productService.updateProduct(productId, request);

        // then
        assertThat(response).isNotNull();
        verify(fileService, never()).uploadFile(any());
    }

    @Test
    @DisplayName("updateProduct 성공 - 이미지 있으면 FileService 호출")
    void updateProduct_Success_WithImage() {
        // given
        Long productId = 1L;
        Product product = createProduct(true);
        MockMultipartFile image = new MockMultipartFile("image", "update.jpg", "image/jpeg", new byte[]{1, 2, 3});
        ProductUpdateRequest request = createProductUpdateRequest(image);

        given(productRepository.findById(productId)).willReturn(Optional.of(product));
        given(fileService.uploadFile(image)).willReturn("http://updated.jpg");

        // when
        ProductResponse response = productService.updateProduct(productId, request);

        // then
        assertThat(response).isNotNull();
        verify(fileService, times(1)).uploadFile(image);
    }

    // ========== deleteProduct ==========

    @Test
    @DisplayName("deleteProduct 성공 - 재고 0인 상품 삭제")
    void deleteProduct_Success() {
        // given
        Long productId = 1L;
        Product product = createProduct(false);
        given(productRepository.findById(productId)).willReturn(Optional.of(product));
        given(stockService.getStock(productId)).willReturn(0);

        // when
        productService.deleteProduct(productId);

        // then
        assertThat(product.getIsDeleted()).isTrue();
    }

    @Test
    @DisplayName("deleteProduct 실패 - 재고가 남아있으면 예외 발생")
    void deleteProduct_Fail_StockRemains() {
        // given
        Long productId = 1L;
        Product product = createProduct(true);
        given(productRepository.findById(productId)).willReturn(Optional.of(product));
        given(stockService.getStock(productId)).willReturn(10);

        // when & then
        assertThatThrownBy(() -> productService.deleteProduct(productId))
            .isInstanceOf(ProductException.class);
    }

    @Test
    @DisplayName("deleteProduct 실패 - 존재하지 않는 상품")
    void deleteProduct_Fail_NotFound() {
        // given
        given(productRepository.findById(anyLong())).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> productService.deleteProduct(1L))
            .isInstanceOf(ProductException.class);
    }

    // ========== getProductForUser ==========

    @Test
    @DisplayName("getProductForUser 성공 - ON_SALE 상품 조회")
    void getProductForUser_Success() {
        // given
        Long productId = 1L;
        Product product = createProduct(true); // ON_SALE
        given(productRepository.findById(productId)).willReturn(Optional.of(product));

        // when
        ProductResponseForUser response = productService.getProductForUser(productId);

        // then
        assertThat(response).isNotNull();
    }

    @Test
    @DisplayName("getProductForUser 실패 - PREPARING 상태 상품")
    void getProductForUser_Fail_Preparing() {
        // given
        Long productId = 1L;
        Product product = createProduct(false); // PREPARING
        given(productRepository.findById(productId)).willReturn(Optional.of(product));

        // when & then
        assertThatThrownBy(() -> productService.getProductForUser(productId))
            .isInstanceOf(ProductException.class);
    }

    // ========== findCartableProduct ==========

    @Test
    @DisplayName("findCartableProduct 성공 - ON_SALE 상품")
    void findCartableProduct_Success() {
        // given
        Long productId = 1L;
        Product product = createProduct(true); // ON_SALE
        given(productRepository.findById(productId)).willReturn(Optional.of(product));

        // when
        Product result = productService.findCartableProduct(productId);

        // then
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("findCartableProduct 실패 - ON_SALE이 아닌 상품")
    void findCartableProduct_Fail_NotOnSale() {
        // given
        Long productId = 1L;
        Product product = createProduct(false); // PREPARING
        given(productRepository.findById(productId)).willReturn(Optional.of(product));

        // when & then
        assertThatThrownBy(() -> productService.findCartableProduct(productId))
            .isInstanceOf(ProductException.class);
    }

    // ========== 헬퍼 메서드 ==========

    /**
     * @param onSale true면 ON_SALE, false면 PREPARING
     */
    private Product createProduct(boolean onSale) {
        Product product = Product.builder()
            .name("테스트 상품")
            .description("설명")
            .price(10000)
            .category(ProductCategory.ELECTRONICS)
            .build();
        if (onSale) {
            product.markOnSale();
        }
        return product;
    }

    private ProductCreateRequest createProductCreateRequest(MockMultipartFile image) {
        ProductCreateRequest request = new ProductCreateRequest();
        ReflectionTestUtils.setField(request, "name", "테스트 상품");
        ReflectionTestUtils.setField(request, "description", "설명");
        ReflectionTestUtils.setField(request, "price", 10000);
        ReflectionTestUtils.setField(request, "stock", 100);
        ReflectionTestUtils.setField(request, "category", ProductCategory.ELECTRONICS);
        ReflectionTestUtils.setField(request, "image", image);
        return request;
    }

    private ProductUpdateRequest createProductUpdateRequest(MockMultipartFile image) {
        ProductUpdateRequest request = new ProductUpdateRequest();
        ReflectionTestUtils.setField(request, "name", "수정된 상품명");
        ReflectionTestUtils.setField(request, "image", image);
        return request;
    }
}
