package com.prj.flashdeal.domain.cart.exception;

import com.prj.flashdeal.global.exception.CustomException;
import com.prj.flashdeal.global.exception.ErrorCode;

public class CartException extends CustomException {
    public CartException(ErrorCode errorCode) {
        super(errorCode);
    }
}
