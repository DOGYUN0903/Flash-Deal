package com.prj.flashdeal.domain.payment.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.prj.flashdeal.domain.member.entity.Member;
import com.prj.flashdeal.domain.order.entity.Order;
import com.prj.flashdeal.domain.order.service.OrderService;
import com.prj.flashdeal.domain.payment.client.TossPaymentClient;
import com.prj.flashdeal.domain.payment.dto.request.PaymentRequest;
import com.prj.flashdeal.domain.payment.dto.request.TossConfirmRequest;
import com.prj.flashdeal.domain.payment.dto.response.PaymentResponse;
import com.prj.flashdeal.domain.payment.entity.Payment;
import com.prj.flashdeal.domain.payment.entity.PaymentMethod;
import com.prj.flashdeal.domain.payment.exception.PaymentException;
import com.prj.flashdeal.domain.payment.repository.PaymentRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("PaymentService 단위 테스트")
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private OrderService orderService;

    @Mock
    private TossPaymentClient tossPaymentClient;

    @InjectMocks
    private PaymentService paymentService;

    // ========== processPayment ==========

    @Test
    @DisplayName("processPayment 성공")
    void processPayment_Success() {
        // given
        Long memberId = 1L;
        Order order = createOrder(memberId, 10000);
        PaymentRequest request = createPaymentRequest(1L, 10000, PaymentMethod.CARD);

        given(orderService.findOrder(1L)).willReturn(order);
        given(paymentRepository.findByOrderId(order.getId())).willReturn(Optional.empty());
        given(paymentRepository.save(any(Payment.class))).willAnswer(inv -> inv.getArgument(0));

        // when
        PaymentResponse response = paymentService.processPayment(memberId, request);

        // then
        assertThat(response).isNotNull();
        verify(paymentRepository, times(1)).save(any(Payment.class));
    }

    @Test
    @DisplayName("processPayment 멱등성 - 이미 결제된 주문은 기존 결제 반환")
    void processPayment_Idempotent() {
        // given
        Long memberId = 1L;
        Order order = createOrder(memberId, 10000);
        Payment existingPayment = createPayment(order, 10000);
        PaymentRequest request = createPaymentRequest(1L, 10000, PaymentMethod.CARD);

        given(orderService.findOrder(1L)).willReturn(order);
        given(paymentRepository.findByOrderId(order.getId())).willReturn(Optional.of(existingPayment));

        // when
        PaymentResponse response = paymentService.processPayment(memberId, request);

        // then
        assertThat(response).isNotNull();
        verify(paymentRepository, times(0)).save(any(Payment.class)); // 새로 저장하지 않음
    }

    @Test
    @DisplayName("processPayment 실패 - 결제 금액 불일치")
    void processPayment_Fail_AmountMismatch() {
        // given
        Long memberId = 1L;
        Order order = createOrder(memberId, 10000);
        PaymentRequest request = createPaymentRequest(1L, 99999, PaymentMethod.CARD); // 금액 다름

        given(orderService.findOrder(1L)).willReturn(order);
        given(paymentRepository.findByOrderId(order.getId())).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> paymentService.processPayment(memberId, request))
            .isInstanceOf(PaymentException.class);
    }

    @Test
    @DisplayName("processPayment 실패 - 주문자 불일치")
    void processPayment_Fail_Unauthorized() {
        // given
        Long memberId = 2L; // 주문자는 1L
        Order order = createOrder(1L, 10000);
        PaymentRequest request = createPaymentRequest(1L, 10000, PaymentMethod.CARD);

        given(orderService.findOrder(1L)).willReturn(order);

        // when & then
        assertThatThrownBy(() -> paymentService.processPayment(memberId, request))
            .isInstanceOf(PaymentException.class);
    }

    // ========== confirmTossPayment ==========

    @Test
    @DisplayName("confirmTossPayment 성공")
    void confirmTossPayment_Success() {
        // given
        Long memberId = 1L;
        Order order = createOrder(memberId, 10000);
        TossConfirmRequest request = createTossConfirmRequest("ORDER-1-uuid123", 10000);

        given(orderService.findOrder(1L)).willReturn(order);
        given(paymentRepository.findByOrderId(order.getId())).willReturn(Optional.empty());
        willDoNothing().given(tossPaymentClient).confirm(any(), any(), any());
        given(paymentRepository.save(any(Payment.class))).willAnswer(inv -> inv.getArgument(0));

        // when
        PaymentResponse response = paymentService.confirmTossPayment(memberId, request);

        // then
        assertThat(response).isNotNull();
        verify(tossPaymentClient, times(1)).confirm(any(), any(), any());
    }

    @Test
    @DisplayName("confirmTossPayment 멱등성 - 이미 결제된 주문은 기존 결제 반환")
    void confirmTossPayment_Idempotent() {
        // given
        Long memberId = 1L;
        Order order = createOrder(memberId, 10000);
        Payment existingPayment = createPayment(order, 10000);
        TossConfirmRequest request = createTossConfirmRequest("ORDER-1-uuid123", 10000);

        given(orderService.findOrder(1L)).willReturn(order);
        given(paymentRepository.findByOrderId(order.getId())).willReturn(Optional.of(existingPayment));

        // when
        PaymentResponse response = paymentService.confirmTossPayment(memberId, request);

        // then
        assertThat(response).isNotNull();
        verify(tossPaymentClient, times(0)).confirm(any(), any(), any()); // Toss API 호출 안 함
    }

    @Test
    @DisplayName("confirmTossPayment 실패 - 결제 금액 불일치")
    void confirmTossPayment_Fail_AmountMismatch() {
        // given
        Long memberId = 1L;
        Order order = createOrder(memberId, 10000);
        TossConfirmRequest request = createTossConfirmRequest("ORDER-1-uuid123", 99999);

        given(orderService.findOrder(1L)).willReturn(order);
        given(paymentRepository.findByOrderId(order.getId())).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> paymentService.confirmTossPayment(memberId, request))
            .isInstanceOf(PaymentException.class);
    }

    // ========== getPayment ==========

    @Test
    @DisplayName("getPayment 성공")
    void getPayment_Success() {
        // given
        Long memberId = 1L;
        Long paymentId = 1L;
        Order order = createOrder(memberId, 10000);
        Payment payment = createPayment(order, 10000);

        given(paymentRepository.findById(paymentId)).willReturn(Optional.of(payment));

        // when
        PaymentResponse response = paymentService.getPayment(memberId, paymentId);

        // then
        assertThat(response).isNotNull();
    }

    @Test
    @DisplayName("getPayment 실패 - 존재하지 않는 결제")
    void getPayment_Fail_NotFound() {
        // given
        given(paymentRepository.findById(anyLong())).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> paymentService.getPayment(1L, 999L))
            .isInstanceOf(PaymentException.class);
    }

    @Test
    @DisplayName("getPayment 실패 - 주문자 불일치")
    void getPayment_Fail_Unauthorized() {
        // given
        Long memberId = 2L; // 주문자는 1L
        Order order = createOrder(1L, 10000);
        Payment payment = createPayment(order, 10000);

        given(paymentRepository.findById(1L)).willReturn(Optional.of(payment));

        // when & then
        assertThatThrownBy(() -> paymentService.getPayment(memberId, 1L))
            .isInstanceOf(PaymentException.class);
    }

    // ========== refundPayment ==========

    @Test
    @DisplayName("refundPayment 성공")
    void refundPayment_Success() {
        // given
        Long memberId = 1L;
        Long paymentId = 1L;
        Order order = createOrder(memberId, 10000);
        Payment payment = createPayment(order, 10000);

        given(paymentRepository.findById(paymentId)).willReturn(Optional.of(payment));
        willDoNothing().given(orderService).cancelOrder(memberId, order.getId());

        // when
        paymentService.refundPayment(memberId, paymentId);

        // then
        assertThat(payment.getStatus().name()).isEqualTo("REFUNDED");
        verify(orderService, times(1)).cancelOrder(memberId, order.getId());
    }

    // ========== 헬퍼 메서드 ==========

    private Member createMember(Long id) {
        Member member = Member.builder()
            .email("test@test.com")
            .password("pw")
            .name("테스터")
            .phoneNumber("010-1234-5678")
            .build();
        ReflectionTestUtils.setField(member, "id", id);
        return member;
    }

    private Order createOrder(Long memberId, int totalPrice) {
        Member member = createMember(memberId);
        Order order = Order.createOrder(member);
        ReflectionTestUtils.setField(order, "id", memberId); // 테스트용 ID
        ReflectionTestUtils.setField(order, "totalPrice", totalPrice);
        return order;
    }

    private Payment createPayment(Order order, int amount) {
        return Payment.builder()
            .order(order)
            .amount(amount)
            .build();
    }

    private PaymentRequest createPaymentRequest(Long orderId, int amount, PaymentMethod method) {
        PaymentRequest request = new PaymentRequest();
        ReflectionTestUtils.setField(request, "orderId", orderId);
        ReflectionTestUtils.setField(request, "amount", amount);
        ReflectionTestUtils.setField(request, "paymentMethod", method);
        return request;
    }

    private TossConfirmRequest createTossConfirmRequest(String orderId, int amount) {
        TossConfirmRequest request = new TossConfirmRequest();
        ReflectionTestUtils.setField(request, "paymentKey", "test-payment-key");
        ReflectionTestUtils.setField(request, "orderId", orderId);
        ReflectionTestUtils.setField(request, "amount", amount);
        return request;
    }
}
