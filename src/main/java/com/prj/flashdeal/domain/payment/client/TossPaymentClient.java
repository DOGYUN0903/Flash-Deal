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
import org.springframework.web.client.RestTemplate;

import com.prj.flashdeal.domain.payment.exception.PaymentErrorCode;
import com.prj.flashdeal.domain.payment.exception.PaymentException;

@Component
public class TossPaymentClient {

    private static final String CONFIRM_URL = "https://api.tosspayments.com/v1/payments/confirm";

    private final String secretKey;
    private final RestTemplate restTemplate;

    public TossPaymentClient(@Value("${toss.secret-key}") String secretKey) {
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
                CONFIRM_URL,
                HttpMethod.POST,
                new HttpEntity<>(body, headers),
                String.class
            );
        } catch (HttpClientErrorException e) {
            throw new PaymentException(PaymentErrorCode.TOSS_CONFIRM_FAILED);
        }
    }
}
