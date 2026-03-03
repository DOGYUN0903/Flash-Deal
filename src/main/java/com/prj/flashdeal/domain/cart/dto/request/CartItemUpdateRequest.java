package com.prj.flashdeal.domain.cart.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "장바구니 수량 수정 요청")
@Getter
@NoArgsConstructor
public class CartItemUpdateRequest {

    @Schema(description = "변경할 수량", example = "3")
    @NotNull(message = "수량은 필수입니다.")
    @Min(value = 1, message = "수량은 1개 이상이어야 합니다.")
    private Integer quantity;
}
