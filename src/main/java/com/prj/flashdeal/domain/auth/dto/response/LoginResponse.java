package com.prj.flashdeal.domain.auth.dto.response;

import com.prj.flashdeal.domain.member.entity.Role;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "로그인/회원가입 응답")
public record LoginResponse(
    @Schema(description = "회원 ID", example = "1") Long userId,
    @Schema(description = "회원 이름", example = "홍길동") String name,
    @Schema(description = "회원 권한 (USER 또는 ADMIN)", example = "USER") Role role
) {
    public static LoginResponse of(Long userId, String name, Role role) {
        return new LoginResponse(userId, name, role);
    }
}
