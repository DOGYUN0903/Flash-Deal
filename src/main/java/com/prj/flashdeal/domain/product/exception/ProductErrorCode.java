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
    INVALID_STOCK_QUANTITY("재고 수량은 0보다 커야합니다.", HttpStatus.BAD_REQUEST),
    INVALID_PRICE("상품 금액은 0보다 커야합니다.", HttpStatus.BAD_REQUEST),
    BLANK_PRODUCT_NAME("상품명은 비워둘 수 없습니다.", HttpStatus.BAD_REQUEST),
    ALREADY_DELETED_PRODUCT("이미 삭제된 상품입니다.", HttpStatus.NOT_FOUND),
    PRODUCT_NOT_ON_SALE("판매중인 상품만 장바구니에 담을 수 있습니다.", HttpStatus.BAD_REQUEST);

    private final String message;
    private final HttpStatus status;

}
