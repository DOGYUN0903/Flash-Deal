package com.prj.flashdeal.domain.auth.dto.response;

public record SignupResponse(
    String grantType,
    String accessToken
) {
    public static SignupResponse of(String accessToken) {
        return new SignupResponse("Bearer", accessToken);
    }
}
