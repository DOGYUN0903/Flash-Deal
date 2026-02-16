package com.prj.flashdeal.domain.deal.exception;

import org.springframework.http.HttpStatus;

import com.prj.flashdeal.global.exception.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DealErrorCode implements ErrorCode {

    DEAL_NOT_FOUND("딜을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    DEAL_NOT_OPEN("현재 오픈되지 않은 딜입니다.", HttpStatus.BAD_REQUEST),
    INSUFFICIENT_STOCK("재고가 부족합니다.", HttpStatus.CONFLICT);

    private final String message;
    private final HttpStatus status;
}
