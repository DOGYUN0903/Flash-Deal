package com.prj.flashdeal.domain.auth.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.prj.flashdeal.domain.auth.dto.request.SignupRequest;
import com.prj.flashdeal.domain.auth.dto.response.SignupResponse;
import com.prj.flashdeal.domain.auth.service.AuthService;
import com.prj.flashdeal.global.response.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<SignupResponse>> signup(@Valid @RequestBody SignupRequest request) {
        return ApiResponse.success(
            HttpStatus.CREATED,
            "회원가입이 완료되었습니다.",
            authService.signup(request)
        );
    }
}
