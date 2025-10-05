package com.prj.flashdeal.domain.cart.exception;

import org.springframework.http.HttpStatus;

import com.prj.flashdeal.global.exception.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CartErrorCode implements ErrorCode {

    INVALID_QUANTITY("수량은 1개 이상이어야 합니다.", HttpStatus.BAD_REQUEST),
    CART_ITEM_NOT_FOUND("장바구니 상품을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    UNAUTHORIZED_CART_ITEM("본인의 장바구니 상품만 수정할 수 있습니다.", HttpStatus.FORBIDDEN);


    private final String message;
    private final HttpStatus status;

}
