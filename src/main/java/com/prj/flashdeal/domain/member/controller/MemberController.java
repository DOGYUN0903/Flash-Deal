package com.prj.flashdeal.domain.member.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.prj.flashdeal.domain.member.dto.request.MemberUpdateRequest;
import com.prj.flashdeal.domain.member.dto.request.PasswordChangeRequest;
import com.prj.flashdeal.domain.member.dto.request.PasswordVerifyRequest;
import com.prj.flashdeal.domain.member.dto.response.MemberProfileResponse;
import com.prj.flashdeal.domain.member.service.MemberService;
import com.prj.flashdeal.global.response.ApiResponse;
import com.prj.flashdeal.global.security.CustomUserDetails;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Member", description = "회원 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;

    /**
     * 프로필 조회
     */
    @Operation(summary = "내 프로필 조회", description = "로그인한 회원의 프로필 정보를 조회합니다.")
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<MemberProfileResponse>> getMyProfile(
        @AuthenticationPrincipal CustomUserDetails userPrincipal
    ) {
        return ApiResponse.success(
            HttpStatus.OK,
            "프로필 조회에 성공하였습니다.",
            memberService.getMyProfile(userPrincipal.getUserId())
        );
    }

    /**
     * 회원 정보 수정
     */
    @Operation(summary = "프로필 수정", description = "이름과 전화번호를 수정합니다.")
    @PatchMapping("/me")
    public ResponseEntity<ApiResponse<MemberProfileResponse>> updateMemberInfo(
        @AuthenticationPrincipal CustomUserDetails userPrincipal,
        @Valid @RequestBody MemberUpdateRequest request
    ) {
        return ApiResponse.success(
            HttpStatus.OK,
            "회원 정보가 수정되었습니다.",
            memberService.updateMemberInfo(userPrincipal.getUserId(), request)
        );
    }

    /**
     * 현재 비밀번호 확인
     */
    @Operation(summary = "비밀번호 확인", description = "현재 비밀번호가 올바른지 확인합니다.")
    @PostMapping("/me/verify-password")
    public ResponseEntity<ApiResponse<Void>> verifyPassword(
        @AuthenticationPrincipal CustomUserDetails userPrincipal,
        @Valid @RequestBody PasswordVerifyRequest request
    ) {
        memberService.verifyPassword(userPrincipal.getUserId(), request.getPassword());
        return ApiResponse.success(HttpStatus.OK, "비밀번호 확인 완료", null);
    }

    /**
     * 비밀번호 변경
     */
    @Operation(summary = "비밀번호 변경", description = "현재 비밀번호 확인 후 새 비밀번호로 변경합니다.")
    @PatchMapping("/me/password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
        @AuthenticationPrincipal CustomUserDetails userPrincipal,
        @Valid @RequestBody PasswordChangeRequest request
    ) {
        memberService.changePassword(userPrincipal.getUserId(), request);
        return ApiResponse.success(
            HttpStatus.OK,
            "비밀번호가 변경되었습니다.",
            null
        );
    }
}
