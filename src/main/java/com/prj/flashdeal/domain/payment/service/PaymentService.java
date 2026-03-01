package com.prj.flashdeal.domain.payment.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prj.flashdeal.domain.order.entity.Order;
import com.prj.flashdeal.domain.order.service.OrderService;
import com.prj.flashdeal.domain.payment.client.TossPaymentClient;
import com.prj.flashdeal.domain.payment.dto.request.PaymentRequest;
import com.prj.flashdeal.domain.payment.dto.request.TossConfirmRequest;
import com.prj.flashdeal.domain.payment.dto.response.PaymentResponse;
import com.prj.flashdeal.domain.payment.entity.Payment;
import com.prj.flashdeal.domain.payment.entity.PaymentMethod;
import com.prj.flashdeal.domain.payment.exception.PaymentErrorCode;
import com.prj.flashdeal.domain.payment.exception.PaymentException;
import com.prj.flashdeal.domain.payment.repository.PaymentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderService orderService;
    private final TossPaymentClient tossPaymentClient;

    /**
     * 결제 처리 (멱등성 보장: 이미 완료된 결제면 기존 결제 반환)
     */
    @Transactional
    public PaymentResponse processPayment(Long memberId, PaymentRequest request) {
        Order order = orderService.findOrder(request.getOrderId());

        validateOrderOwner(order, memberId);

        Optional<Payment> existing = paymentRepository.findByOrderId(order.getId());
        if (existing.isPresent()) {
            return PaymentResponse.from(existing.get());
        }

        // 결제 금액 검증 (보안: 클라이언트가 보낸 금액과 실제 주문 금액 일치 확인)
        if (!request.getAmount().equals(order.getTotalPrice())) {
            throw new PaymentException(PaymentErrorCode.PAYMENT_AMOUNT_MISMATCH);
        }

        Payment payment = Payment.builder()
            .order(order)
            .amount(request.getAmount())
            .build();
        payment.completePayment(request.getPaymentMethod());
        order.completePayment(payment);

        return PaymentResponse.from(paymentRepository.save(payment));
    }

    /**
     * 토스페이먼츠 결제 승인 (멱등성 보장: 이미 완료된 결제면 기존 결제 반환)
     */
    @Transactional
    public PaymentResponse confirmTossPayment(Long memberId, TossConfirmRequest request) {
        Long orderId = parseOrderId(request.getOrderId());

        Order order = orderService.findOrder(orderId);
        validateOrderOwner(order, memberId);

        Optional<Payment> existing = paymentRepository.findByOrderId(orderId);
        if (existing.isPresent()) {
            return PaymentResponse.from(existing.get());
        }

        // 결제 금액 검증 (보안: 클라이언트가 보낸 금액과 실제 주문 금액 일치 확인)
        if (!request.getAmount().equals(order.getTotalPrice())) {
            throw new PaymentException(PaymentErrorCode.PAYMENT_AMOUNT_MISMATCH);
        }

        tossPaymentClient.confirm(request.getPaymentKey(), request.getOrderId(), request.getAmount());

        Payment payment = Payment.builder()
            .order(order)
            .amount(request.getAmount())
            .build();
        payment.completePayment(PaymentMethod.TOSS);
        order.completePayment(payment);

        return PaymentResponse.from(paymentRepository.save(payment));
    }

    /**
     * 결제 조회
     */
    @Transactional(readOnly = true)
    public PaymentResponse getPayment(Long memberId, Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
            .orElseThrow(() -> new PaymentException(PaymentErrorCode.PAYMENT_NOT_FOUND));

        validateOrderOwner(payment.getOrder(), memberId);

        return PaymentResponse.from(payment);
    }

    /**
     * 주문 ID로 결제 조회
     */
    @Transactional(readOnly = true)
    public PaymentResponse getPaymentByOrderId(Long memberId, Long orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId)
            .orElseThrow(() -> new PaymentException(PaymentErrorCode.PAYMENT_NOT_FOUND));

        validateOrderOwner(payment.getOrder(), memberId);

        return PaymentResponse.from(payment);
    }

    /**
     * 환불 처리 (OrderService를 통한 주문 취소)
     */
    @Transactional
    public void refundPayment(Long memberId, Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
            .orElseThrow(() -> new PaymentException(PaymentErrorCode.PAYMENT_NOT_FOUND));

        validateOrderOwner(payment.getOrder(), memberId);

        payment.refund();
        orderService.cancelOrder(memberId, payment.getOrder().getId());
    }

    // ---------------- private 헬퍼 메서드 ----------------

    /**
     * "ORDER-{id}-{uuid}" 형식에서 DB orderId 파싱
     */
    private Long parseOrderId(String tossOrderId) {
        try {
            String withoutPrefix = tossOrderId.replaceFirst("^ORDER-", "");
            return Long.parseLong(withoutPrefix.split("-")[0]);
        } catch (NumberFormatException e) {
            throw new PaymentException(PaymentErrorCode.PAYMENT_NOT_FOUND);
        }
    }

    private void validateOrderOwner(Order order, Long memberId) {
        if (!order.getMember().getId().equals(memberId)) {
            throw new PaymentException(PaymentErrorCode.UNAUTHORIZED_ORDER);
        }
    }

}
