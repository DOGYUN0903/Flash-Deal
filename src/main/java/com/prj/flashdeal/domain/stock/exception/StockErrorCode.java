package com.prj.flashdeal.domain.stock.exception;

import org.springframework.http.HttpStatus;

import com.prj.flashdeal.global.exception.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum StockErrorCode implements ErrorCode {

    STOCK_NOT_FOUND("재고 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    OUT_OF_STOCK("재고가 부족합니다.", HttpStatus.CONFLICT),
    INVALID_STOCK_QUANTITY("재고 수량이 유효하지 않습니다.", HttpStatus.BAD_REQUEST);

    private final String message;
    private final HttpStatus status;
}
