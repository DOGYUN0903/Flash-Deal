package com.prj.flashdeal.domain.member.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.prj.flashdeal.domain.member.dto.response.MemberProfileResponse;
import com.prj.flashdeal.domain.member.service.MemberService;
import com.prj.flashdeal.global.response.ApiResponse;
import com.prj.flashdeal.global.security.dto.UserPrincipal;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<MemberProfileResponse>> getMyProfile(
        @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ApiResponse.success(
            HttpStatus.OK,
            "프로필 조회에 성공하였습니다.",
            memberService.getMyProfile(userPrincipal.getUserId())
        );
    }
}
