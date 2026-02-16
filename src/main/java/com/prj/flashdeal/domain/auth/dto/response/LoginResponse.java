package com.prj.flashdeal.domain.auth.dto.response;

import com.prj.flashdeal.domain.member.entity.Role;

public record LoginResponse(
    Long userId,
    String name,
    Role role
) {
    public static LoginResponse of(Long userId, String name, Role role) {
        return new LoginResponse(userId, name, role);
    }
}
