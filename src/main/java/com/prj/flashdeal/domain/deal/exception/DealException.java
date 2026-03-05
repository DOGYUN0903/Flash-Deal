package com.prj.flashdeal.domain.deal.exception;

import com.prj.flashdeal.global.exception.CustomException;
import com.prj.flashdeal.global.exception.ErrorCode;

public class DealException extends CustomException {
    public DealException(ErrorCode errorCode) {
        super(errorCode);
    }
}
