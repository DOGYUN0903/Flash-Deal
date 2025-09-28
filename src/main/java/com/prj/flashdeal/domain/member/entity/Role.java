package com.prj.flashdeal.domain.member.entity;

import java.util.Arrays;

import com.prj.flashdeal.domain.member.exception.MemberErrorCode;
import com.prj.flashdeal.domain.member.exception.MemberException;

public enum Role {
    USER, ADMIN;

    public static Role of(String role) {
        return Arrays.stream(Role.values())
            .filter(r -> r.name().equalsIgnoreCase(role))
            .findFirst()
            .orElseThrow(() -> new MemberException(MemberErrorCode.INVALID_USER_ROLE));
    }
}
