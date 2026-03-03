package com.prj.flashdeal.domain.member.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import com.prj.flashdeal.domain.member.entity.Member;

@Schema(description = "회원 프로필 응답")
public record MemberProfileResponse(
    @Schema(description = "회원 ID", example = "1") Long id,
    @Schema(description = "이메일 주소", example = "user1@test.com") String email,
    @Schema(description = "이름", example = "홍길동") String name,
    @Schema(description = "전화번호", example = "010-1234-5678") String phoneNumber
) {
    public static MemberProfileResponse from(Member member) {
        return new MemberProfileResponse(
            member.getId(),
            member.getEmail(),
            member.getName(),
            member.getPhoneNumber()
        );
    }
}
