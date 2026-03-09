package com.prj.flashdeal.domain.payment.client;

import java.util.Random;

import org.springframework.stereotype.Component;

import com.prj.flashdeal.domain.payment.exception.PaymentErrorCode;
import com.prj.flashdeal.domain.payment.exception.PaymentException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class FakePaymentClient {

    private static final int MIN_DELAY_MS = 500;
    private static final int MAX_DELAY_MS = 1000;
    private final Random random = new Random();

    /**
     * 외부 PG사 결제를 시뮬레이션하는 Mock 메서드.
     * 500~1000ms 지연을 발생시켜 외부 통신 지연을 흉내냄.
     */
    public void pay(Long memberId, Integer amount) {
        simulateLatency();
        log.info("Mock 결제 성공 - memberId: {}, amount: {}", memberId, amount);
    }

    private void simulateLatency() {
        try {
            int delay = MIN_DELAY_MS + random.nextInt(MAX_DELAY_MS - MIN_DELAY_MS + 1);
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new PaymentException(PaymentErrorCode.MOCK_PAYMENT_FAILED);
        }
    }
}
