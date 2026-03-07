package com.prj.flashdeal.domain.deal.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "딜 주문 요청")
@Getter
@Setter
@NoArgsConstructor
public class DealOrderRequest {

    @Schema(description = "결제 금액", example = "50000")
    @NotNull(message = "결제 금액은 필수입니다.")
    private Integer amount;

    @Schema(description = "주문 수량", example = "1")
    @NotNull(message = "수량은 필수입니다.")
    @Min(value = 1, message = "수량은 1 이상이어야 합니다.")
    private Integer quantity;
}
