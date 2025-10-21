package com.prj.flashdeal.domain.payment.exception;

import com.prj.flashdeal.global.exception.CustomException;
import com.prj.flashdeal.global.exception.ErrorCode;

public class PaymentException extends CustomException {
    public PaymentException(ErrorCode errorCode) {
        super(errorCode);
    }
}
