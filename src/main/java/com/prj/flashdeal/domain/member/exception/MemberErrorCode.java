package com.prj.flashdeal.domain.member.exception;

import org.springframework.http.HttpStatus;

import com.prj.flashdeal.global.exception.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MemberErrorCode implements ErrorCode {

    INVALID_USER_ROLE("유효하지 않은 유저 권한입니다.", HttpStatus.BAD_REQUEST),
    ALREADY_EXISTS_EMAIL("이미 존재하는 이메일입니다.", HttpStatus.CONFLICT);

    private final String message;
    private final HttpStatus status;

}
