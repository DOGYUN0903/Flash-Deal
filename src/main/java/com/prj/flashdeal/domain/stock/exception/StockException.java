package com.prj.flashdeal.domain.stock.exception;

import com.prj.flashdeal.global.exception.CustomException;
import com.prj.flashdeal.global.exception.ErrorCode;

public class StockException extends CustomException {
    public StockException(ErrorCode errorCode) {
        super(errorCode);
    }
}
