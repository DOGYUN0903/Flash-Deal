package com.prj.flashdeal.domain.payment.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TossConfirmRequest {

    @NotBlank
    private String paymentKey;

    @NotBlank
    private String orderId; // Toss 결제 고유 식별자 "ORDER-{orderId}-{uuid}" 형식

    @NotNull
    private Integer amount;
}
