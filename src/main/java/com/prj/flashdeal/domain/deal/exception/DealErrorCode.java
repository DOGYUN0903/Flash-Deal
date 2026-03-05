package com.prj.flashdeal.domain.deal.exception;

import org.springframework.http.HttpStatus;

import com.prj.flashdeal.global.exception.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DealErrorCode implements ErrorCode {

    DEAL_NOT_FOUND("딜을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    DEAL_NOT_ACTIVE("진행 중인 딜이 아닙니다.", HttpStatus.BAD_REQUEST),
    DEAL_PAYMENT_AMOUNT_MISMATCH("결제 금액이 딜 가격과 일치하지 않습니다.", HttpStatus.BAD_REQUEST);

    private final String message;
    private final HttpStatus status;
}
