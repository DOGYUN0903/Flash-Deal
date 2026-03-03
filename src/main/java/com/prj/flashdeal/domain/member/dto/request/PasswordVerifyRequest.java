package com.prj.flashdeal.domain.member.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "비밀번호 확인 요청")
@Getter
@NoArgsConstructor
public class PasswordVerifyRequest {

    @Schema(description = "확인할 현재 비밀번호", example = "CurrentPassword1!")
    @NotBlank
    private String password;
}
