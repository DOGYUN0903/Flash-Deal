package com.prj.flashdeal.domain.payment.client;

import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import com.prj.flashdeal.domain.payment.dto.response.TossPaymentResponse;
import com.prj.flashdeal.domain.payment.exception.PaymentErrorCode;
import com.prj.flashdeal.domain.payment.exception.PaymentException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class TossPaymentClient {

    private final RestClient tossRestClient;

    public TossPaymentResponse confirm(String paymentKey, String orderId, Integer amount) {
        try {
            return tossRestClient.post()
                .uri("/v1/payments/confirm")
                .header("Idempotency-Key", orderId)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("paymentKey", paymentKey, "orderId", orderId, "amount", amount))
                .retrieve()
                .body(TossPaymentResponse.class);
        } catch (HttpClientErrorException e) {
            log.error("Toss 결제 승인 실패 - status: {}, body: {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new PaymentException(PaymentErrorCode.TOSS_CONFIRM_FAILED);
        } catch (RestClientException e) {
            log.error("Toss API 통신 오류: {}", e.getMessage());
            throw new PaymentException(PaymentErrorCode.TOSS_CONFIRM_FAILED);
        }
    }

    public void cancel(String paymentKey, String cancelReason) {
        try {
            tossRestClient.post()
                .uri("/v1/payments/{paymentKey}/cancel", paymentKey)
                .header("Idempotency-Key", paymentKey + "-cancel")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("cancelReason", cancelReason))
                .retrieve()
                .toBodilessEntity();
        } catch (RestClientException e) {
            log.error("Toss 결제 취소 실패 - paymentKey: {}, reason: {}", paymentKey, e.getMessage());
            throw new PaymentException(PaymentErrorCode.TOSS_CANCEL_FAILED);
        }
    }
}
