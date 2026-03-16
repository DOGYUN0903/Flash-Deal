package com.prj.flashdeal.domain.payment.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;

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
import com.prj.flashdeal.domain.payment.dto.response.TossPaymentResponse;
import com.prj.flashdeal.domain.payment.entity.Payment;
import com.prj.flashdeal.domain.payment.entity.PaymentMethod;
import com.prj.flashdeal.domain.payment.entity.PaymentStatus;
import com.prj.flashdeal.domain.payment.exception.PaymentException;
import com.prj.flashdeal.domain.payment.repository.PaymentRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("PaymentService unit tests")
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private OrderService orderService;

    @Mock
    private TossPaymentClient tossPaymentClient;

    @Mock
    private PaymentSaveService paymentSaveService;

    @InjectMocks
    private PaymentService paymentService;

    @Test
    @DisplayName("processPayment saves a new payment")
    void processPayment_Success() {
        Order order = createOrder(1L, 1L, 10000);
        PaymentRequest request = createPaymentRequest(1L, 10000, PaymentMethod.CARD);

        given(orderService.findOrder(1L)).willReturn(order);
        given(paymentRepository.findByOrderId(1L)).willReturn(Optional.empty());
        given(paymentRepository.save(any(Payment.class))).willAnswer(invocation -> {
            Payment payment = invocation.getArgument(0);
            ReflectionTestUtils.setField(payment, "id", 5L);
            return payment;
        });

        PaymentResponse response = paymentService.processPayment(1L, request);

        assertThat(response.paymentId()).isEqualTo(5L);
        assertThat(response.amount()).isEqualTo(10000);
    }

    @Test
    @DisplayName("processPayment returns existing payment for duplicate requests")
    void processPayment_Idempotent() {
        Order order = createOrder(1L, 1L, 10000);
        Payment existingPayment = createCompletedPayment(order, 5L, 10000, PaymentMethod.CARD);
        PaymentRequest request = createPaymentRequest(1L, 10000, PaymentMethod.CARD);

        given(orderService.findOrder(1L)).willReturn(order);
        given(paymentRepository.findByOrderId(1L)).willReturn(Optional.of(existingPayment));

        PaymentResponse response = paymentService.processPayment(1L, request);

        assertThat(response.paymentId()).isEqualTo(5L);
        then(paymentRepository).should().findByOrderId(1L);
    }

    @Test
    @DisplayName("processPayment rejects mismatched amount")
    void processPayment_Fail_AmountMismatch() {
        Order order = createOrder(1L, 1L, 10000);
        PaymentRequest request = createPaymentRequest(1L, 9999, PaymentMethod.CARD);

        given(orderService.findOrder(1L)).willReturn(order);
        given(paymentRepository.findByOrderId(1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> paymentService.processPayment(1L, request))
            .isInstanceOf(PaymentException.class);
    }

    @Test
    @DisplayName("confirmTossPayment saves confirmed payment through PaymentSaveService")
    void confirmTossPayment_Success() {
        Order order = createOrder(1L, 1L, 10000);
        TossConfirmRequest request = createTossConfirmRequest("ORDER-1-uuid123", 10000);
        TossPaymentResponse tossResponse = new TossPaymentResponse();
        ReflectionTestUtils.setField(tossResponse, "paymentKey", "tgen_key");
        PaymentResponse savedResponse = new PaymentResponse(
            7L,
            1L,
            PaymentStatus.COMPLETED,
            PaymentMethod.TOSS,
            10000,
            null,
            null
        );

        given(orderService.findOrder(1L)).willReturn(order);
        given(paymentRepository.findByOrderId(1L)).willReturn(Optional.empty());
        given(tossPaymentClient.confirm("test-payment-key", "ORDER-1-uuid123", 10000)).willReturn(tossResponse);
        given(paymentSaveService.saveConfirmedPayment(1L, 10000, "tgen_key")).willReturn(savedResponse);

        PaymentResponse response = paymentService.confirmTossPayment(1L, request);

        assertThat(response.paymentId()).isEqualTo(7L);
        then(paymentSaveService).should().saveConfirmedPayment(1L, 10000, "tgen_key");
    }

    @Test
    @DisplayName("getPayment returns owned payment")
    void getPayment_Success() {
        Order order = createOrder(1L, 1L, 10000);
        Payment payment = createCompletedPayment(order, 5L, 10000, PaymentMethod.CARD);

        given(paymentRepository.findById(5L)).willReturn(Optional.of(payment));

        PaymentResponse response = paymentService.getPayment(1L, 5L);

        assertThat(response.paymentId()).isEqualTo(5L);
    }

    @Test
    @DisplayName("refundPayment cancels Toss payment and order")
    void refundPayment_Success() {
        Order order = createOrder(1L, 1L, 10000);
        Payment payment = createCompletedPayment(order, 5L, 10000, PaymentMethod.TOSS);
        ReflectionTestUtils.setField(payment, "tossPaymentKey", "tgen_key");

        given(paymentRepository.findById(5L)).willReturn(Optional.of(payment));
        willDoNothing().given(tossPaymentClient).cancel("tgen_key", "고객 환불 요청");
        willDoNothing().given(orderService).cancelOrder(1L, 1L);

        paymentService.refundPayment(1L, 5L);

        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.REFUNDED);
        then(orderService).should().cancelOrder(1L, 1L);
    }

    private Order createOrder(Long memberId, Long orderId, int totalPrice) {
        Member member = Member.builder()
            .email("test@test.com")
            .password("pw")
            .name("tester")
            .phoneNumber("010-1234-5678")
            .build();
        ReflectionTestUtils.setField(member, "id", memberId);

        Order order = Order.createOrder(member);
        ReflectionTestUtils.setField(order, "id", orderId);
        ReflectionTestUtils.setField(order, "totalPrice", totalPrice);
        return order;
    }

    private Payment createCompletedPayment(Order order, Long paymentId, int amount, PaymentMethod method) {
        Payment payment = Payment.builder()
            .order(order)
            .amount(amount)
            .build();
        ReflectionTestUtils.setField(payment, "id", paymentId);
        payment.completePayment(method);
        return payment;
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
