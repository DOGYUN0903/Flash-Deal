package com.prj.flashdeal.domain.member.exception;

import com.prj.flashdeal.global.exception.CustomException;
import com.prj.flashdeal.global.exception.ErrorCode;

public class MemberException extends CustomException {
    public MemberException(ErrorCode errorCode) {
        super(errorCode);
    }
}
