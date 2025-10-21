package com.prj.flashdeal.domain.order.exception;

import com.prj.flashdeal.global.exception.CustomException;
import com.prj.flashdeal.global.exception.ErrorCode;

public class OrderException extends CustomException {
    public OrderException(ErrorCode errorCode) {
        super(errorCode);
    }
}
