package com.prj.flashdeal.global.config;

import java.time.Duration;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.http.client.ClientHttpRequestFactoryBuilder;
import org.springframework.boot.http.client.ClientHttpRequestFactorySettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    /**
     * 토스페이먼츠 전용 RestClient
     * - Connection Timeout: 5초 (일반 권장값)
     * - Read Timeout: 60초 (토스페이먼츠 결제 API 공식 권장값)
     *   https://docs.tosspayments.com/resources/glossary/timeout
     */
    @Bean
    public RestClient tossRestClient(
        @Value("${toss.base-url}") String baseUrl,
        @Value("${toss.secret-key}") String secretKey
    ) {
        String credentials = Base64.getEncoder()
            .encodeToString((secretKey + ":").getBytes());

        ClientHttpRequestFactorySettings settings = ClientHttpRequestFactorySettings.defaults()
            .withConnectTimeout(Duration.ofSeconds(5))
            .withReadTimeout(Duration.ofSeconds(60));

        ClientHttpRequestFactory factory = ClientHttpRequestFactoryBuilder.detect().build(settings);

        return RestClient.builder()
            .requestFactory(factory)
            .baseUrl(baseUrl)
            .defaultHeader("Authorization", "Basic " + credentials)
            .build();
    }
}
