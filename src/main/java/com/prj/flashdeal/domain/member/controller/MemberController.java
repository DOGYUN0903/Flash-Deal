package com.prj.flashdeal.domain.member.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.prj.flashdeal.domain.member.dto.request.MemberUpdateRequest;
import com.prj.flashdeal.domain.member.dto.request.PasswordChangeRequest;
import com.prj.flashdeal.domain.member.dto.response.MemberProfileResponse;
import com.prj.flashdeal.domain.member.service.MemberService;
import com.prj.flashdeal.global.response.ApiResponse;
import com.prj.flashdeal.global.security.dto.UserPrincipal;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;

    /**
     * 프로필 조회
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<MemberProfileResponse>> getMyProfile(
        @AuthenticationPrincipal UserPrincipal userPrincipal
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
    @PatchMapping("/me")
    public ResponseEntity<ApiResponse<MemberProfileResponse>> updateMemberInfo(
        @AuthenticationPrincipal UserPrincipal userPrincipal,
        @Valid @RequestBody MemberUpdateRequest request
    ) {
        return ApiResponse.success(
            HttpStatus.OK,
            "회원 정보가 수정되었습니다.",
            memberService.updateMemberInfo(userPrincipal.getUserId(), request)
        );
    }

    /**
     * 비밀번호 변경
     */
    @PatchMapping("/me/password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
        @AuthenticationPrincipal UserPrincipal userPrincipal,
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
