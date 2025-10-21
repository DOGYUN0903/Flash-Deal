package com.prj.flashdeal.domain.payment.exception;

import org.springframework.http.HttpStatus;

import com.prj.flashdeal.global.exception.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PaymentErrorCode implements ErrorCode {

    PAYMENT_NOT_FOUND("결제 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    PAYMENT_ALREADY_COMPLETED("이미 완료된 결제입니다.", HttpStatus.BAD_REQUEST),
    PAYMENT_ALREADY_FAILED("이미 실패한 결제입니다.", HttpStatus.BAD_REQUEST),
    INVALID_PAYMENT_STATUS("잘못된 결제 상태입니다.", HttpStatus.BAD_REQUEST),
    PAYMENT_AMOUNT_MISMATCH("결제 금액이 일치하지 않습니다.", HttpStatus.BAD_REQUEST),
    ORDER_NOT_FOUND("주문을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    UNAUTHORIZED_ORDER("본인의 주문만 접근할 수 있습니다.", HttpStatus.FORBIDDEN);

    private final String message;
    private final HttpStatus status;
}
