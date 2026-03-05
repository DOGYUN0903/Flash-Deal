package com.prj.flashdeal.domain.payment.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 토스페이먼츠 결제 승인 API 응답 객체
 * 필요한 필드만 매핑하고 나머지는 무시
 */
@Schema(description = "토스페이먼츠 결제 승인 응답")
@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TossPaymentResponse {

    @Schema(description = "토스 결제 키", example = "tgen_20240101_abc123")
    private String paymentKey;

    @Schema(description = "주문 ID", example = "ORDER-1-550e8400")
    private String orderId;

    @Schema(description = "결제 상태", example = "DONE")
    private String status;
}
