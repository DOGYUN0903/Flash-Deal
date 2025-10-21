package com.prj.flashdeal.domain.payment.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prj.flashdeal.domain.order.entity.Order;
import com.prj.flashdeal.domain.order.service.OrderService;
import com.prj.flashdeal.domain.payment.dto.request.PaymentRequest;
import com.prj.flashdeal.domain.payment.dto.response.PaymentResponse;
import com.prj.flashdeal.domain.payment.entity.Payment;
import com.prj.flashdeal.domain.payment.exception.PaymentErrorCode;
import com.prj.flashdeal.domain.payment.exception.PaymentException;
import com.prj.flashdeal.domain.payment.repository.PaymentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderService orderService;

    /**
     * 결제 처리
     */
    @Transactional
    public PaymentResponse processPayment(Long memberId, PaymentRequest request) {
        // 주문 조회 및 권한 검증
        Order order = orderService.findOrder(request.getOrderId());

        if (!order.getMember().getId().equals(memberId)) {
            throw new PaymentException(PaymentErrorCode.UNAUTHORIZED_ORDER);
        }

        // 이미 결제가 존재하는지 확인
        paymentRepository.findByOrderId(order.getId())
            .ifPresent(payment -> {
                throw new PaymentException(PaymentErrorCode.PAYMENT_ALREADY_COMPLETED);
            });

        // 결제 금액 검증 (보안: 클라이언트가 보낸 금액과 실제 주문 금액 일치 확인)
        if (!request.getAmount().equals(order.getTotalPrice())) {
            throw new PaymentException(PaymentErrorCode.PAYMENT_AMOUNT_MISMATCH);
        }

        // 결제 생성
        Payment payment = Payment.builder()
            .order(order)
            .amount(request.getAmount())
            .build();

        // 결제 완료 처리
        payment.completePayment(request.getPaymentMethod());

        // 주문 상태 변경 (PENDING → PAID) 및 양방향 연관관계 설정
        order.completePayment(payment);

        Payment savedPayment = paymentRepository.save(payment);

        return PaymentResponse.from(savedPayment);
    }

    /**
     * 결제 조회
     */
    @Transactional(readOnly = true)
    public PaymentResponse getPayment(Long memberId, Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
            .orElseThrow(() -> new PaymentException(PaymentErrorCode.PAYMENT_NOT_FOUND));

        // 권한 검증
        if (!payment.getOrder().getMember().getId().equals(memberId)) {
            throw new PaymentException(PaymentErrorCode.UNAUTHORIZED_ORDER);
        }

        return PaymentResponse.from(payment);
    }

    /**
     * 주문 ID로 결제 조회
     */
    @Transactional(readOnly = true)
    public PaymentResponse getPaymentByOrderId(Long memberId, Long orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId)
            .orElseThrow(() -> new PaymentException(PaymentErrorCode.PAYMENT_NOT_FOUND));

        // 권한 검증
        if (!payment.getOrder().getMember().getId().equals(memberId)) {
            throw new PaymentException(PaymentErrorCode.UNAUTHORIZED_ORDER);
        }

        return PaymentResponse.from(payment);
    }

    /**
     * 환불 처리 (OrderService를 통한 주문 취소)
     */
    @Transactional
    public void refundPayment(Long memberId, Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
            .orElseThrow(() -> new PaymentException(PaymentErrorCode.PAYMENT_NOT_FOUND));

        // 권한 검증
        if (!payment.getOrder().getMember().getId().equals(memberId)) {
            throw new PaymentException(PaymentErrorCode.UNAUTHORIZED_ORDER);
        }

        // 환불 처리
        payment.refund();

        // 주문 취소 (OrderService를 통한 재고 복구)
        orderService.cancelOrder(memberId, payment.getOrder().getId());
    }
}
