package com.prj.flashdeal.domain.member.exception;

import org.springframework.http.HttpStatus;

import com.prj.flashdeal.global.exception.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MemberErrorCode implements ErrorCode {

    INVALID_USER_ROLE("유효하지 않은 유저 권한입니다.", HttpStatus.BAD_REQUEST),
    ALREADY_EXISTS_EMAIL("이미 존재하는 이메일입니다.", HttpStatus.CONFLICT),
    MEMBER_NOT_FOUND("존재하지 않는 회원입니다.", HttpStatus.NOT_FOUND),
    INVALID_PASSWORD("비밀번호가 일치하지 않습니다.", HttpStatus.BAD_REQUEST),
    INACTIVE_MEMBER("비활성화된 계정입니다.", HttpStatus.FORBIDDEN),
    INVALID_CHARGE_AMOUNT("충전 금액은 0보다 커야 합니다.", HttpStatus.BAD_REQUEST),
    INVALID_USE_AMOUNT("사용 금액은 0보다 커야 합니다.", HttpStatus.BAD_REQUEST),
    INSUFFICIENT_BALANCE("잔액이 부족합니다.", HttpStatus.BAD_REQUEST);

    private final String message;
    private final HttpStatus status;

}
