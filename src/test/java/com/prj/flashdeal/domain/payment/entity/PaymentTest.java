package com.prj.flashdeal.domain.payment.entity;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.prj.flashdeal.domain.member.entity.Member;
import com.prj.flashdeal.domain.order.entity.Order;

@DisplayName("Payment 엔티티 단위 테스트")
class PaymentTest {

    // ========== 빌더 기본값 ==========

    @Test
    @DisplayName("빌더로 생성한 Payment의 기본 상태는 PENDING")
    void builder_DefaultStatus_IsPending() {
        // when
        Payment payment = createPayment(10000);

        // then
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.PENDING);
        assertThat(payment.getAmount()).isEqualTo(10000);
        assertThat(payment.getPaidAt()).isNull();
    }

    // ========== completePayment ==========

    @Test
    @DisplayName("completePayment - COMPLETED 상태, 결제 수단과 결제 시각이 설정됨")
    void completePayment_Success() {
        // given
        Payment payment = createPayment(10000);

        // when
        payment.completePayment(PaymentMethod.TOSS);

        // then
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.COMPLETED);
        assertThat(payment.getMethod()).isEqualTo(PaymentMethod.TOSS);
        assertThat(payment.getPaidAt()).isNotNull();
    }

    // ========== failPayment ==========

    @Test
    @DisplayName("failPayment - FAILED 상태로 변경됨")
    void failPayment_Success() {
        // given
        Payment payment = createPayment(10000);

        // when
        payment.failPayment();

        // then
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.FAILED);
    }

    // ========== refund ==========

    @Test
    @DisplayName("refund - REFUNDED 상태로 변경됨")
    void refund_Success() {
        // given
        Payment payment = createPayment(10000);
        payment.completePayment(PaymentMethod.CARD);

        // when
        payment.refund();

        // then
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.REFUNDED);
    }

    // ========== 헬퍼 메서드 ==========

    private Payment createPayment(int amount) {
        Member member = Member.builder()
            .email("test@test.com")
            .password("pw")
            .name("테스터")
            .phoneNumber("010-1234-5678")
            .build();

        Order order = Order.createOrder(member);

        return Payment.builder()
            .order(order)
            .amount(amount)
            .build();
    }
}
