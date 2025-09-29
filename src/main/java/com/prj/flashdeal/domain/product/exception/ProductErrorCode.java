package com.prj.flashdeal.domain.product.exception;

import org.springframework.http.HttpStatus;

import com.prj.flashdeal.global.exception.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProductErrorCode implements ErrorCode {

    STOCK_REMAINS("재고가 남아있어서 삭제가 불가능합니다.", HttpStatus.CONFLICT),
    PRODUCT_NOT_FOUND("상품을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    INVALID_STOCK_QUANTITY("재고 수량은 0보다 커야합니다.", HttpStatus.BAD_REQUEST);

    private final String message;
    private final HttpStatus status;

}
