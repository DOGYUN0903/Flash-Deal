package com.prj.flashdeal.domain.product.exception;

import org.springframework.http.HttpStatus;

import com.prj.flashdeal.global.exception.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProductErrorCode implements ErrorCode {

    STOCK_REMAINS("재고가 남아있어서 삭제가 불가능합니다.", HttpStatus.CONFLICT);

    private final String message;
    private final HttpStatus status;

}
