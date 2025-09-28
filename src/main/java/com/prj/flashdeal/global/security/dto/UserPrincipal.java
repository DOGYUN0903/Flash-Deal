package com.prj.flashdeal.global.security.dto;

import com.prj.flashdeal.domain.member.entity.Role;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class UserPrincipal {

    private final Long userId;

    private final Role userRole;
}
