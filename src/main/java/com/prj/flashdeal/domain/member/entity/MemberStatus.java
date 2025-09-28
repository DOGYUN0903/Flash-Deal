package com.prj.flashdeal.domain.member.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MemberStatus {

    ACTIVE("활동"),       // 활동중인 정상 회원
    DORMANT("휴면"),      // 1년 이상 미접속하여 휴면 처리된 회원
    WITHDRAWN("탈퇴"),    // 스스로 탈퇴한 회원
    BANNED("정지");       // 관리자에 의해 정지된 회원

    private final String description;
}
