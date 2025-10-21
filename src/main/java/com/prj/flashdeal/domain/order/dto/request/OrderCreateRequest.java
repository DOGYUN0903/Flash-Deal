package com.prj.flashdeal.domain.order.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OrderCreateRequest {

    @NotBlank(message = "수령인 이름은 필수입니다.")
    private String recipientName;

    @NotBlank(message = "전화번호는 필수입니다.")
    private String phoneNumber;

    @NotBlank(message = "우편번호는 필수입니다.")
    private String zipcode;

    @NotBlank(message = "주소는 필수입니다.")
    private String street;

    @NotBlank(message = "상세주소는 필수입니다.")
    private String detail;
}
