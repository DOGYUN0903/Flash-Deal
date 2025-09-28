package com.prj.flashdeal.domain.auth.dto.response;

public record TokenResponse(
    String grantType,
    String accessToken
) {
    public static TokenResponse of(String accessToken) {
        return new TokenResponse("Bearer", accessToken);
    }
}
