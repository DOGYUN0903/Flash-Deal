package com.prj.flashdeal.domain.order.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.prj.flashdeal.domain.member.entity.Member;
import com.prj.flashdeal.domain.payment.entity.Payment;
import com.prj.flashdeal.domain.payment.entity.PaymentMethod;
import com.prj.flashdeal.domain.product.entity.Product;
import com.prj.flashdeal.domain.product.entity.ProductCategory;
import com.prj.flashdeal.domain.order.exception.OrderException;

@DisplayName("Order 엔티티 단위 테스트")
class OrderTest {

    // ========== createOrder ==========

    @Test
    @DisplayName("createOrder - PENDING 상태, totalPrice=0으로 생성됨")
    void createOrder_DefaultValues() {
        // when
        Order order = Order.createOrder(createMember());

        // then
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PENDING);
        assertThat(order.getTotalPrice()).isEqualTo(0);
        assertThat(order.getOrderItems()).isEmpty();
    }

    // ========== addOrderItem ==========

    @Test
    @DisplayName("addOrderItem - totalPrice에 주문 항목 금액이 합산됨")
    void addOrderItem_AccumulatesTotalPrice() {
        // given
        Order order = Order.createOrder(createMember());
        Product product = createProduct(10000);
        OrderItem orderItem = OrderItem.createOrderItem(product, 3);

        // when
        order.addOrderItem(orderItem);

        // then
        assertThat(order.getTotalPrice()).isEqualTo(30000);
        assertThat(order.getOrderItems()).hasSize(1);
    }

    @Test
    @DisplayName("addOrderItem - 여러 항목 추가 시 totalPrice가 누적됨")
    void addOrderItem_MultipleItems_AccumulatesTotalPrice() {
        // given
        Order order = Order.createOrder(createMember());
        OrderItem item1 = OrderItem.createOrderItem(createProduct(10000), 2); // 20000
        OrderItem item2 = OrderItem.createOrderItem(createProduct(5000), 3);  // 15000

        // when
        order.addOrderItem(item1);
        order.addOrderItem(item2);

        // then
        assertThat(order.getTotalPrice()).isEqualTo(35000);
        assertThat(order.getOrderItems()).hasSize(2);
    }

    // ========== cancel ==========

    @Test
    @DisplayName("cancel 성공 - PENDING 상태에서 CANCELED로 변경")
    void cancel_Success_FromPending() {
        // given
        Order order = Order.createOrder(createMember());

        // when
        order.cancel();

        // then
        assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELED);
    }

    @Test
    @DisplayName("cancel 실패 - DELIVERED 상태에서 취소 불가")
    void cancel_Fail_Delivered() {
        // given
        Order order = createDeliveredOrder();

        // when & then
        assertThatThrownBy(() -> order.cancel())
            .isInstanceOf(OrderException.class);
    }

    // ========== completePayment ==========

    @Test
    @DisplayName("completePayment 성공 - PENDING → PAID, Payment 연관관계 설정")
    void completePayment_Success() {
        // given
        Order order = Order.createOrder(createMember());
        Payment payment = Payment.builder()
            .order(order)
            .amount(10000)
            .build();

        // when
        order.completePayment(payment);

        // then
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PAID);
        assertThat(order.getPayment()).isEqualTo(payment);
    }

    @Test
    @DisplayName("completePayment 실패 - PENDING이 아닌 상태에서 예외 발생")
    void completePayment_Fail_NotPending() {
        // given - 이미 결제 완료된 주문
        Order order = Order.createOrder(createMember());
        Payment payment = Payment.builder().order(order).amount(10000).build();
        order.completePayment(payment);

        // when & then
        assertThatThrownBy(() -> order.completePayment(payment))
            .isInstanceOf(OrderException.class);
    }

    // ========== ship ==========

    @Test
    @DisplayName("ship 성공 - PAID → SHIPPED")
    void ship_Success() {
        // given
        Order order = createPaidOrder();

        // when
        order.ship();

        // then
        assertThat(order.getStatus()).isEqualTo(OrderStatus.SHIPPED);
    }

    @Test
    @DisplayName("ship 실패 - PAID가 아닌 상태에서 예외 발생")
    void ship_Fail_NotPaid() {
        // given - PENDING 상태
        Order order = Order.createOrder(createMember());

        // when & then
        assertThatThrownBy(() -> order.ship())
            .isInstanceOf(OrderException.class);
    }

    // ========== deliver ==========

    @Test
    @DisplayName("deliver 성공 - SHIPPED → DELIVERED")
    void deliver_Success() {
        // given
        Order order = createPaidOrder();
        order.ship();

        // when
        order.deliver();

        // then
        assertThat(order.getStatus()).isEqualTo(OrderStatus.DELIVERED);
    }

    @Test
    @DisplayName("deliver 실패 - SHIPPED가 아닌 상태에서 예외 발생")
    void deliver_Fail_NotShipped() {
        // given - PAID 상태
        Order order = createPaidOrder();

        // when & then
        assertThatThrownBy(() -> order.deliver())
            .isInstanceOf(OrderException.class);
    }

    // ========== 헬퍼 메서드 ==========

    private Member createMember() {
        return Member.builder()
            .email("test@test.com")
            .password("pw")
            .name("테스터")
            .phoneNumber("010-1234-5678")
            .build();
    }

    private Product createProduct(int price) {
        return Product.builder()
            .name("테스트 상품")
            .description("설명")
            .price(price)
            .stock(100)
            .category(ProductCategory.ELECTRONICS)
            .build();
    }

    private Order createPaidOrder() {
        Order order = Order.createOrder(createMember());
        Payment payment = Payment.builder().order(order).amount(10000).build();
        order.completePayment(payment);
        return order;
    }

    private Order createDeliveredOrder() {
        Order order = createPaidOrder();
        order.ship();
        order.deliver();
        return order;
    }
}
