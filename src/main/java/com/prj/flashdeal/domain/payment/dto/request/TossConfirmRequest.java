package com.prj.flashdeal.domain.payment.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "토스페이먼츠 결제 승인 요청")
@Getter
@NoArgsConstructor
public class TossConfirmRequest {

    @Schema(description = "토스페이먼츠 결제 키", example = "tgen_20240101_xxxxx")
    @NotBlank
    private String paymentKey;

    @Schema(description = "주문 ID (형식: ORDER-{id}-{uuid})", example = "ORDER-1-abc123")
    @NotBlank
    private String orderId; // Toss 결제 고유 식별자 "ORDER-{orderId}-{uuid}" 형식

    @Schema(description = "결제 금액", example = "50000")
    @NotNull
    private Integer amount;
}
