package com.prj.flashdeal.domain.deal.dto.request;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "딜 등록 요청")
@Getter
@Setter
@NoArgsConstructor
public class DealCreateRequest {

    @Schema(description = "상품 ID", example = "1")
    @NotNull(message = "상품 ID는 필수입니다.")
    private Long productId;

    @Schema(description = "딜 제목", example = "나이키 에어맥스 90 한정 할인!")
    @NotBlank(message = "딜 제목은 필수입니다.")
    private String title;

    @Schema(description = "할인 가격 (원)", example = "89000")
    @NotNull(message = "할인 가격은 필수입니다.")
    @Positive(message = "할인 가격은 0보다 커야 합니다.")
    private Integer discountPrice;

    @Schema(description = "딜 시작 시간", example = "2026-03-10T10:00:00")
    @NotNull(message = "딜 시작 시간은 필수입니다.")
    private LocalDateTime startAt;

    @Schema(description = "딜 종료 시간", example = "2026-03-10T12:00:00")
    @NotNull(message = "딜 종료 시간은 필수입니다.")
    private LocalDateTime endAt;
}
