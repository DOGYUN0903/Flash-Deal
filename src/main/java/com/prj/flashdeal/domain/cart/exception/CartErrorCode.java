package com.prj.flashdeal.domain.cart.exception;

import org.springframework.http.HttpStatus;

import com.prj.flashdeal.global.exception.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CartErrorCode implements ErrorCode {

    INVALID_QUANTITY("수량은 1개 이상이어야 합니다.", HttpStatus.BAD_REQUEST);


    private final String message;
    private final HttpStatus status;

}
