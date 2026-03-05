package com.prj.flashdeal.domain.cart.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.prj.flashdeal.domain.cart.dto.request.CartItemAddRequest;
import com.prj.flashdeal.domain.cart.dto.request.CartItemUpdateRequest;
import com.prj.flashdeal.domain.cart.dto.response.CartItemResponse;
import com.prj.flashdeal.domain.cart.dto.response.CartResponse;
import com.prj.flashdeal.domain.cart.entity.CartItem;
import com.prj.flashdeal.domain.cart.exception.CartException;
import com.prj.flashdeal.domain.cart.repository.CartItemRepository;
import com.prj.flashdeal.domain.member.entity.Member;
import com.prj.flashdeal.domain.member.service.MemberService;
import com.prj.flashdeal.domain.product.entity.Product;
import com.prj.flashdeal.domain.product.service.ProductService;

@ExtendWith(MockitoExtension.class)
@DisplayName("CartService 단위 테스트")
class CartServiceTest {

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private MemberService memberService;

    @Mock
    private ProductService productService;

    @InjectMocks
    private CartService cartService;

    @Test
    @DisplayName("장바구니에 상품 추가 성공 - 새로운 상품")
    void addCartItem_Success_NewProduct() {
        // given
        Long memberId = 1L;
        Long productId = 1L;
        int quantity = 2;

        Member member = createMember(memberId);
        Product product = createProduct(productId, 10000, 100);
        CartItemAddRequest request = new CartItemAddRequest();
        ReflectionTestUtils.setField(request, "productId", productId);
        ReflectionTestUtils.setField(request, "quantity", quantity);

        given(memberService.getMember(memberId)).willReturn(member);
        given(productService.findCartableProduct(productId)).willReturn(product);
        given(cartItemRepository.findByMemberAndProduct(member, product)).willReturn(Optional.empty());
        given(cartItemRepository.save(any(CartItem.class))).willAnswer(invocation -> invocation.getArgument(0));

        // when
        CartItemResponse response = cartService.addCartItem(memberId, request);

        // then
        assertThat(response).isNotNull();
        assertThat(response.quantity()).isEqualTo(quantity);
        verify(cartItemRepository, times(1)).save(any(CartItem.class));
    }

    @Test
    @DisplayName("장바구니에 상품 추가 성공 - 기존 상품 수량 증가")
    void addCartItem_Success_ExistingProduct() {
        // given
        Long memberId = 1L;
        Long productId = 1L;
        int existingQuantity = 2;
        int addQuantity = 3;

        Member member = createMember(memberId);
        Product product = createProduct(productId, 10000, 100);
        CartItem existingCartItem = CartItem.builder()
            .member(member)
            .product(product)
            .quantity(existingQuantity)
            .build();

        CartItemAddRequest request = new CartItemAddRequest();
        ReflectionTestUtils.setField(request, "productId", productId);
        ReflectionTestUtils.setField(request, "quantity", addQuantity);

        given(memberService.getMember(memberId)).willReturn(member);
        given(productService.findCartableProduct(productId)).willReturn(product);
        given(cartItemRepository.findByMemberAndProduct(member, product)).willReturn(Optional.of(existingCartItem));

        // when
        CartItemResponse response = cartService.addCartItem(memberId, request);

        // then
        assertThat(response).isNotNull();
        assertThat(response.quantity()).isEqualTo(existingQuantity + addQuantity);
        verify(cartItemRepository, never()).save(any(CartItem.class));
    }

    @Test
    @DisplayName("장바구니 조회 성공")
    void getCartItems_Success() {
        // given
        Long memberId = 1L;
        Member member = createMember(memberId);

        List<CartItemResponse> mockCartItems = List.of(
            new CartItemResponse(1L, 1L, "상품1", 10000, 2, 20000),
            new CartItemResponse(2L, 2L, "상품2", 15000, 1, 15000)
        );

        given(memberService.getMember(memberId)).willReturn(member);
        given(cartItemRepository.findCartItemsByMember(member)).willReturn(mockCartItems);

        // when
        CartResponse response = cartService.getCartItems(memberId);

        // then
        assertThat(response).isNotNull();
        assertThat(response.memberId()).isEqualTo(memberId);
        assertThat(response.items()).hasSize(2);
        assertThat(response.totalPrice()).isEqualTo(35000);
    }

    @Test
    @DisplayName("장바구니 상품 수량 수정 성공")
    void updateCartItemQuantity_Success() {
        // given
        Long memberId = 1L;
        Long cartItemId = 1L;
        int newQuantity = 5;

        Member member = createMember(memberId);
        Product product = createProduct(1L, 10000, 100);
        CartItem cartItem = CartItem.builder()
            .member(member)
            .product(product)
            .quantity(2)
            .build();

        CartItemUpdateRequest request = new CartItemUpdateRequest();
        ReflectionTestUtils.setField(request, "quantity", newQuantity);

        given(memberService.getMember(memberId)).willReturn(member);
        given(cartItemRepository.findById(cartItemId)).willReturn(Optional.of(cartItem));

        // when
        CartItemResponse response = cartService.updateCartItemQuantity(memberId, cartItemId, request);

        // then
        assertThat(response).isNotNull();
        assertThat(response.quantity()).isEqualTo(newQuantity);
    }

    @Test
    @DisplayName("장바구니 상품 수량 수정 실패 - 권한 없음")
    void updateCartItemQuantity_Fail_Unauthorized() {
        // given
        Long memberId = 1L;
        Long otherMemberId = 2L;
        Long cartItemId = 1L;

        Member member = createMember(memberId);
        Member otherMember = createMember(otherMemberId);
        Product product = createProduct(1L, 10000, 100);
        CartItem cartItem = CartItem.builder()
            .member(otherMember)  // 다른 회원의 장바구니
            .product(product)
            .quantity(2)
            .build();

        CartItemUpdateRequest request = new CartItemUpdateRequest();
        ReflectionTestUtils.setField(request, "quantity", 5);

        given(memberService.getMember(memberId)).willReturn(member);
        given(cartItemRepository.findById(cartItemId)).willReturn(Optional.of(cartItem));

        // when & then
        assertThatThrownBy(() -> cartService.updateCartItemQuantity(memberId, cartItemId, request))
            .isInstanceOf(CartException.class);
    }

    @Test
    @DisplayName("장바구니 상품 삭제 성공")
    void deleteCartItem_Success() {
        // given
        Long memberId = 1L;
        Long cartItemId = 1L;

        Member member = createMember(memberId);
        Product product = createProduct(1L, 10000, 100);
        CartItem cartItem = CartItem.builder()
            .member(member)
            .product(product)
            .quantity(2)
            .build();

        given(memberService.getMember(memberId)).willReturn(member);
        given(cartItemRepository.findById(cartItemId)).willReturn(Optional.of(cartItem));
        willDoNothing().given(cartItemRepository).delete(cartItem);

        // when
        cartService.deleteCartItem(memberId, cartItemId);

        // then
        verify(cartItemRepository, times(1)).delete(cartItem);
    }

    @Test
    @DisplayName("장바구니 전체 비우기 성공")
    void clearCart_Success() {
        // given
        Long memberId = 1L;
        Member member = createMember(memberId);

        given(memberService.getMember(memberId)).willReturn(member);
        willDoNothing().given(cartItemRepository).deleteAllByMember(member);

        // when
        cartService.clearCart(memberId);

        // then
        verify(cartItemRepository, times(1)).deleteAllByMember(member);
    }

    // ========== 헬퍼 메서드 ==========

    private Member createMember(Long id) {
        Member member = Member.builder()
            .email("test@test.com")
            .password("password123")
            .name("테스트")
            .build();
        ReflectionTestUtils.setField(member, "id", id);
        return member;
    }

    private Product createProduct(Long id, int price, int stockQuantity) {
        Product product = Product.builder()
            .name("테스트 상품")
            .description("테스트 상품 설명")
            .price(price)
            .build();
        ReflectionTestUtils.setField(product, "id", id);
        if (stockQuantity > 0) {
            product.markOnSale();
        }
        return product;
    }
}
