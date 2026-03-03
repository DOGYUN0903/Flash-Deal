package com.prj.flashdeal.domain.payment.dto.request;

import com.prj.flashdeal.domain.payment.entity.PaymentMethod;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "결제 요청")
@Getter
@NoArgsConstructor
public class PaymentRequest {

    @Schema(description = "주문 ID", example = "1")
    @NotNull(message = "주문 ID는 필수입니다.")
    private Long orderId;

    @Schema(description = "결제 수단", example = "CARD")
    @NotNull(message = "결제 방법은 필수입니다.")
    private PaymentMethod paymentMethod;

    @Schema(description = "결제 금액", example = "50000")
    @NotNull(message = "결제 금액은 필수입니다.")
    @Positive(message = "결제 금액은 0보다 커야 합니다.")
    private Integer amount;
}
