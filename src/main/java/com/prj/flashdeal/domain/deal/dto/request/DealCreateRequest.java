package com.prj.flashdeal.domain.deal.dto.request;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class DealCreateRequest {

    @NotNull(message = "상품 ID는 필수입니다.")
    private Long productId;

    @NotNull(message = "딜 가격은 필수입니다.")
    @Min(value = 1, message = "딜 가격은 1원 이상이어야 합니다.")
    private Integer dealPrice;

    @NotNull(message = "재고는 필수입니다.")
    @Min(value = 1, message = "재고는 1개 이상이어야 합니다.")
    private Integer stock;

    @NotNull(message = "오픈 시간은 필수입니다.")
    private LocalDateTime openTime;

    @NotNull(message = "종료 시간은 필수입니다.")
    @Future(message = "종료 시간은 현재 시간 이후여야 합니다.")
    private LocalDateTime endTime;
}
