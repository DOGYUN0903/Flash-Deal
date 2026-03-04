package com.prj.flashdeal.domain.payment.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prj.flashdeal.domain.order.entity.Order;
import com.prj.flashdeal.domain.order.service.OrderService;
import com.prj.flashdeal.domain.payment.dto.response.PaymentResponse;
import com.prj.flashdeal.domain.payment.entity.Payment;
import com.prj.flashdeal.domain.payment.entity.PaymentMethod;
import com.prj.flashdeal.domain.payment.repository.PaymentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentSaveService {

    private final PaymentRepository paymentRepository;
    private final OrderService orderService;

    /**
     * 결제 승인 후 DB 저장 — PaymentService와 트랜잭션을 분리하기 위해 별도 빈으로 관리.
     * confirmTossPayment()는 @Transactional 없이 호출하고, 이 메서드만 독립 트랜잭션으로 커밋된다.
     */
    @Transactional
    public PaymentResponse saveConfirmedPayment(Long orderId, Integer amount, String tossPaymentKey) {
        Order order = orderService.findOrder(orderId);

        Payment payment = Payment.builder()
            .order(order)
            .amount(amount)
            .build();
        payment.completePayment(PaymentMethod.TOSS, tossPaymentKey);
        order.completePayment(payment);

        return PaymentResponse.from(paymentRepository.save(payment));
    }
}
