package com.prj.flashdeal.domain.deal.exception;

import com.prj.flashdeal.global.exception.CustomException;

public class DealException extends CustomException {

    public DealException(DealErrorCode errorCode) {
        super(errorCode);
    }
}
