package com.prj.flashdeal.domain.auth.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.prj.flashdeal.domain.auth.dto.request.LoginRequest;
import com.prj.flashdeal.domain.auth.dto.request.SignupRequest;
import com.prj.flashdeal.domain.auth.dto.response.LoginResponse;
import com.prj.flashdeal.domain.auth.service.AuthService;
import com.prj.flashdeal.global.response.ApiResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<LoginResponse>> signup(@Valid @RequestBody SignupRequest request) {
        return ApiResponse.success(
            HttpStatus.CREATED,
            "회원가입이 완료되었습니다.",
            authService.signup(request)
        );
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.success(
            HttpStatus.OK,
            "로그인이 완료되었습니다.",
            authService.login(request)
        );
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return ApiResponse.success(HttpStatus.OK, "로그아웃이 완료되었습니다.", null);
    }
}
