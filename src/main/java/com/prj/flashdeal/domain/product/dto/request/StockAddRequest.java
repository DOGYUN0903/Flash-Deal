package com.prj.flashdeal.domain.product.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class StockAddRequest {

    @NotNull(message = "수량은 필수 입력 값입니다.")
    @Positive(message = "수량은 0보다 커야합니다.")
    private Integer quantity;
}
