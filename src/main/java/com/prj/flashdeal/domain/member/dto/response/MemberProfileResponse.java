package com.prj.flashdeal.domain.member.dto.response;

import com.prj.flashdeal.domain.member.entity.Member;

public record MemberProfileResponse(
    Long id,
    String email,
    String name,
    String phoneNumber,
    Long balance
) {
    public static MemberProfileResponse from(Member member) {
        return new MemberProfileResponse(
            member.getId(),
            member.getEmail(),
            member.getName(),
            member.getPhoneNumber(),
            member.getBalance()
        );
    }
}
