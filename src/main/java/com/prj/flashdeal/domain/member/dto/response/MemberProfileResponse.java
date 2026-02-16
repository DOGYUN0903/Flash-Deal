package com.prj.flashdeal.domain.member.dto.response;

import com.prj.flashdeal.domain.member.entity.Address;
import com.prj.flashdeal.domain.member.entity.Member;

public record MemberProfileResponse(
    Long id,
    String email,
    String name,
    Address address,
    String phoneNumber,
    Long balance
) {
    public static MemberProfileResponse from(Member member) {
        return new MemberProfileResponse(
            member.getId(),
            member.getEmail(),
            member.getName(),
            member.getAddress(),
            member.getPhoneNumber(),
            member.getBalance()
        );
    }
}
