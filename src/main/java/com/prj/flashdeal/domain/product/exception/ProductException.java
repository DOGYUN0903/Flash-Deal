package com.prj.flashdeal.domain.product.exception;

import com.prj.flashdeal.global.exception.CustomException;
import com.prj.flashdeal.global.exception.ErrorCode;

public class ProductException extends CustomException {
    public ProductException(ErrorCode errorCode) {
        super(errorCode);
    }
}
