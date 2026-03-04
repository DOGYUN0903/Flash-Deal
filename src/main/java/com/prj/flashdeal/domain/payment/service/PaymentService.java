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
import com.prj.flashdeal.domain.payment.dto.response.TossPaymentResponse;
import com.prj.flashdeal.domain.payment.entity.Payment;
import com.prj.flashdeal.domain.payment.exception.PaymentErrorCode;
import com.prj.flashdeal.domain.payment.exception.PaymentException;
import com.prj.flashdeal.domain.payment.repository.PaymentRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderService orderService;
    private final TossPaymentClient tossPaymentClient;
    private final PaymentSaveService paymentSaveService;

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
     * 토스페이먼츠 결제 승인
     *
     * Toss confirm(외부 API)과 DB 저장을 하나의 @Transactional로 묶으면 안 된다.
     * Toss가 승인됐는데 DB 저장이 실패하면 → 사용자는 결제됐지만 주문 기록이 없는 불일치 발생.
     *
     * 해결: 두 단계로 분리
     *   1단계) Toss confirm (TX 없음) → 실패 시 주문 취소 + 재고 복구
     *   2단계) DB 저장 (@Transactional) → 실패 시 Toss 취소 API 호출 (보상 트랜잭션) + 주문 취소
     */
    public PaymentResponse confirmTossPayment(Long memberId, TossConfirmRequest request) {
        Long orderId = parseOrderId(request.getOrderId());

        Order order = orderService.findOrder(orderId);
        validateOrderOwner(order, memberId);

        Optional<Payment> existing = paymentRepository.findByOrderId(orderId);
        if (existing.isPresent()) {
            return PaymentResponse.from(existing.get());
        }

        if (!request.getAmount().equals(order.getTotalPrice())) {
            throw new PaymentException(PaymentErrorCode.PAYMENT_AMOUNT_MISMATCH);
        }

        // 1단계: Toss 결제 승인 (외부 API, TX 없음)
        // 실패 시 Toss에서 결제가 안 된 것이므로 우리 주문만 취소하면 됨
        TossPaymentResponse tossResponse;
        try {
            tossResponse = tossPaymentClient.confirm(request.getPaymentKey(), request.getOrderId(), request.getAmount());
        } catch (Exception e) {
            orderService.cancelOrderOnPaymentFailure(memberId, orderId);
            throw new PaymentException(PaymentErrorCode.TOSS_CONFIRM_FAILED);
        }

        // 2단계: DB 저장 (독립 @Transactional)
        // 실패 시 Toss는 결제됐으므로 → Toss 취소 API(보상 트랜잭션) + 주문 취소
        try {
            return paymentSaveService.saveConfirmedPayment(orderId, request.getAmount(), tossResponse.getPaymentKey());
        } catch (Exception e) {
            log.error("Toss 승인 후 DB 저장 실패 - paymentKey: {}", request.getPaymentKey());
            try {
                tossPaymentClient.cancel(request.getPaymentKey(), "결제 처리 중 서버 오류");
            } catch (Exception cancelEx) {
                log.error("보상 트랜잭션 실패 - Toss 취소도 실패, 수동 처리 필요 - paymentKey: {}", request.getPaymentKey());
            }
            orderService.cancelOrderOnPaymentFailure(memberId, orderId);
            throw new PaymentException(PaymentErrorCode.TOSS_CONFIRM_FAILED);
        }
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
     * 환불 처리
     * tossPaymentKey가 있으면 Toss 결제 취소 API를 먼저 호출하고, 성공 시 DB 상태 변경.
     * Toss 취소 실패 시 예외를 전파하여 DB 상태가 변경되지 않도록 한다.
     */
    @Transactional
    public void refundPayment(Long memberId, Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
            .orElseThrow(() -> new PaymentException(PaymentErrorCode.PAYMENT_NOT_FOUND));

        validateOrderOwner(payment.getOrder(), memberId);

        if (payment.getTossPaymentKey() != null) {
            tossPaymentClient.cancel(payment.getTossPaymentKey(), "고객 환불 요청");
        }

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
