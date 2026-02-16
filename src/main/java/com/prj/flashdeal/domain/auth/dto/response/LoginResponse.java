package com.prj.flashdeal.domain.auth.dto.response;

public record LoginResponse(
    Long userId,
    String email,
    String name
) {
    public static LoginResponse of(Long userId, String email, String name) {
        return new LoginResponse(userId, email, name);
    }
}
