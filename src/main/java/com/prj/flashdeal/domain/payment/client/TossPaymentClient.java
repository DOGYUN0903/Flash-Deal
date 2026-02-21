package com.prj.flashdeal.domain.payment.client;

import java.util.Base64;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.prj.flashdeal.domain.payment.exception.PaymentErrorCode;
import com.prj.flashdeal.domain.payment.exception.PaymentException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class TossPaymentClient {

    private final String baseUrl;
    private final String secretKey;
    private final RestTemplate restTemplate;

    public TossPaymentClient(
        @Value("${toss.base-url}") String baseUrl,
        @Value("${toss.secret-key}") String secretKey
    ) {
        this.baseUrl = baseUrl;
        this.secretKey = secretKey;
        this.restTemplate = new RestTemplate();
    }

    public void confirm(String paymentKey, String orderId, Integer amount) {
        String credentials = Base64.getEncoder()
            .encodeToString((secretKey + ":").getBytes());

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Basic " + credentials);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = Map.of(
            "paymentKey", paymentKey,
            "orderId", orderId,
            "amount", amount
        );

        try {
            restTemplate.exchange(
                baseUrl + "/v1/payments/confirm",
                HttpMethod.POST,
                new HttpEntity<>(body, headers),
                String.class
            );
        } catch (HttpClientErrorException e) {
            log.error("Toss 결제 승인 실패 - status: {}, body: {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new PaymentException(PaymentErrorCode.TOSS_CONFIRM_FAILED);
        } catch (RestClientException e) {
            log.error("Toss API 통신 오류: {}", e.getMessage());
            throw new PaymentException(PaymentErrorCode.TOSS_CONFIRM_FAILED);
        }
    }
}
