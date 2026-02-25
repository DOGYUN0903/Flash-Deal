package com.prj.flashdeal.domain.order.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.prj.flashdeal.domain.cart.entity.CartItem;
import com.prj.flashdeal.domain.cart.service.CartService;
import com.prj.flashdeal.domain.member.entity.Member;
import com.prj.flashdeal.domain.member.service.MemberService;
import com.prj.flashdeal.domain.order.dto.request.DirectOrderRequest;
import com.prj.flashdeal.domain.order.dto.request.OrderCreateRequest;
import com.prj.flashdeal.domain.order.dto.response.OrderResponse;
import com.prj.flashdeal.domain.order.entity.Order;
import com.prj.flashdeal.domain.order.exception.OrderException;
import com.prj.flashdeal.domain.order.repository.OrderRepository;
import com.prj.flashdeal.domain.product.entity.Product;
import com.prj.flashdeal.domain.product.entity.ProductStatus;
import com.prj.flashdeal.domain.product.service.ProductService;

@ExtendWith(MockitoExtension.class)
@DisplayName("OrderService 단위 테스트")
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private MemberService memberService;

    @Mock
    private CartService cartService;

    @Mock
    private ProductService productService;

    @InjectMocks
    private OrderService orderService;

    @Test
    @DisplayName("장바구니에서 주문 생성 성공")
    void createOrderFromCart_Success() {
        // given
        Long memberId = 1L;
        Member member = createMember(memberId);
        Product product = createProduct(1L, 10000, 100);

        CartItem cartItem = CartItem.builder()
            .member(member)
            .product(product)
            .quantity(2)
            .build();

        OrderCreateRequest request = new OrderCreateRequest();
        setField(request, "recipientName", "홍길동");
        setField(request, "phoneNumber", "010-1234-5678");
        setField(request, "zipcode", "12345");
        setField(request, "street", "서울시 강남구");
        setField(request, "detail", "101동 101호");

        given(memberService.getMember(memberId)).willReturn(member);
        given(cartService.getCartItemsForOrder(member)).willReturn(List.of(cartItem));
        given(orderRepository.save(any(Order.class))).willAnswer(invocation -> invocation.getArgument(0));
        willDoNothing().given(productService).decreaseStock(anyLong(), anyInt());
        willDoNothing().given(cartService).clearCartForOrder(member);

        // when
        OrderResponse response = orderService.createOrderFromCart(memberId, request);

        // then
        assertThat(response).isNotNull();
        assertThat(response.deliveryAddress().recipientName()).isEqualTo("홍길동");
        assertThat(response.totalPrice()).isEqualTo(20000);
        verify(productService, times(1)).decreaseStock(product.getId(), 2);
        verify(cartService, times(1)).clearCartForOrder(member);
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    @DisplayName("장바구니에서 주문 생성 실패 - 빈 장바구니")
    void createOrderFromCart_Fail_EmptyCart() {
        // given
        Long memberId = 1L;
        Member member = createMember(memberId);

        OrderCreateRequest request = new OrderCreateRequest();
        setField(request, "recipientName", "홍길동");
        setField(request, "phoneNumber", "010-1234-5678");
        setField(request, "zipcode", "12345");
        setField(request, "street", "서울시 강남구");

        given(memberService.getMember(memberId)).willReturn(member);
        given(cartService.getCartItemsForOrder(member)).willReturn(List.of());

        // when & then
        assertThatThrownBy(() -> orderService.createOrderFromCart(memberId, request))
            .isInstanceOf(OrderException.class);
    }

    @Test
    @DisplayName("바로 구매 성공")
    void createDirectOrder_Success() {
        // given
        Long memberId = 1L;
        Long productId = 1L;
        int quantity = 3;

        Member member = createMember(memberId);
        Product product = createProduct(productId, 15000, 50);

        DirectOrderRequest request = new DirectOrderRequest();
        setField(request, "productId", productId);
        setField(request, "quantity", quantity);
        setField(request, "recipientName", "김철수");
        setField(request, "phoneNumber", "010-9876-5432");
        setField(request, "zipcode", "54321");
        setField(request, "street", "부산시 해운대구");
        setField(request, "detail", "202동 202호");

        given(memberService.getMember(memberId)).willReturn(member);
        given(productService.findCartableProduct(productId)).willReturn(product);
        given(orderRepository.save(any(Order.class))).willAnswer(invocation -> invocation.getArgument(0));
        willDoNothing().given(productService).decreaseStock(productId, quantity);

        // when
        OrderResponse response = orderService.createDirectOrder(memberId, request);

        // then
        assertThat(response).isNotNull();
        assertThat(response.deliveryAddress().recipientName()).isEqualTo("김철수");
        assertThat(response.totalPrice()).isEqualTo(15000 * 3);
        verify(productService, times(1)).decreaseStock(productId, quantity);
        verify(orderRepository, times(1)).save(any(Order.class));
        verify(cartService, never()).clearCartForOrder(any());  // 바로 구매는 장바구니 비우지 않음
    }

    @Test
    @DisplayName("주문 취소 성공")
    void cancelOrder_Success() {
        // given
        Long memberId = 1L;
        Long orderId = 1L;
        Long productId = 1L;
        int quantity = 2;

        Member member = createMember(memberId);
        Product product = createProduct(productId, 10000, 100);

        Order order = createOrderWithProduct(orderId, member, product, quantity);

        given(orderRepository.findByIdAndMemberId(orderId, memberId)).willReturn(Optional.of(order));
        willDoNothing().given(productService).increaseStock(productId, quantity);

        // when
        orderService.cancelOrder(memberId, orderId);

        // then
        verify(productService, times(1)).increaseStock(productId, quantity);
    }

    @Test
    @DisplayName("주문 조회 성공")
    void getOrder_Success() {
        // given
        Long memberId = 1L;
        Long orderId = 1L;

        Member member = createMember(memberId);
        Product product = createProduct(1L, 10000, 100);
        Order order = createOrderWithProduct(orderId, member, product, 2);

        given(orderRepository.findByIdAndMemberId(orderId, memberId)).willReturn(Optional.of(order));

        // when
        OrderResponse response = orderService.getOrder(memberId, orderId);

        // then
        assertThat(response).isNotNull();
        assertThat(response.totalPrice()).isEqualTo(20000);
    }

    @Test
    @DisplayName("주문 조회 실패 - 존재하지 않는 주문")
    void getOrder_Fail_NotFound() {
        // given
        Long memberId = 1L;
        Long orderId = 999L;

        given(orderRepository.findByIdAndMemberId(orderId, memberId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> orderService.getOrder(memberId, orderId))
            .isInstanceOf(OrderException.class);
    }

    // 테스트 픽스처 생성 헬퍼 메서드
    private Member createMember(Long id) {
        Member member = Member.builder()
            .email("test@test.com")
            .password("password123")
            .name("테스터")
            .build();

        setField(member, "id", id);
        return member;
    }

    private Product createProduct(Long id, int price, int stockQuantity) {
        Product product = Product.builder()
            .name("테스트 상품")
            .description("테스트 상품 설명")
            .price(price)
            .build();

        setField(product, "id", id);
        product.addStock(stockQuantity);

        return product;
    }

    private Order createOrderWithProduct(Long orderId, Member member, Product product, int quantity) {
        Order order = Order.createOrder(
            member,
            com.prj.flashdeal.domain.order.entity.DeliveryAddress.of(
                "홍길동",
                "010-1234-5678",
                "12345",
                "서울시 강남구",
                "101동"
            )
        );

        setField(order, "id", orderId);

        // OrderItem 추가
        com.prj.flashdeal.domain.order.entity.OrderItem orderItem =
            com.prj.flashdeal.domain.order.entity.OrderItem.createOrderItem(product, quantity);
        order.addOrderItem(orderItem);

        return order;
    }

    // Reflection 헬퍼 메서드
    private void setField(Object target, String fieldName, Object value) {
        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (NoSuchFieldException e) {
            // 상위 클래스에서 찾기
            try {
                Field field = target.getClass().getSuperclass().getDeclaredField(fieldName);
                field.setAccessible(true);
                field.set(target, value);
            } catch (Exception ex) {
                throw new RuntimeException("Failed to set field: " + fieldName, ex);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to set field: " + fieldName, e);
        }
    }
}
