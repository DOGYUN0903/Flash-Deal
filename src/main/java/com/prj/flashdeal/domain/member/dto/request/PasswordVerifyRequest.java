package com.prj.flashdeal.domain.member.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PasswordVerifyRequest {

    @NotBlank
    private String password;
}
