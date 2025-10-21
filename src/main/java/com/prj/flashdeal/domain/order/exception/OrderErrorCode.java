package com.prj.flashdeal.domain.order.exception;

import org.springframework.http.HttpStatus;

import com.prj.flashdeal.global.exception.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OrderErrorCode implements ErrorCode {

    ORDER_NOT_FOUND("주문을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    UNAUTHORIZED_ORDER("본인의 주문만 조회할 수 있습니다.", HttpStatus.FORBIDDEN),
    EMPTY_CART("장바구니가 비어있습니다.", HttpStatus.BAD_REQUEST),
    INVALID_ORDER_STATUS("잘못된 주문 상태입니다.", HttpStatus.BAD_REQUEST),
    CANNOT_CANCEL_DELIVERED_ORDER("배송 완료된 주문은 취소할 수 없습니다.", HttpStatus.BAD_REQUEST),
    INSUFFICIENT_STOCK("재고가 부족합니다.", HttpStatus.BAD_REQUEST),
    INVALID_PAYMENT_COMPLETE_STATUS("대기 중인 주문만 결제 완료 처리할 수 있습니다.", HttpStatus.BAD_REQUEST);

    private final String message;
    private final HttpStatus status;
}
