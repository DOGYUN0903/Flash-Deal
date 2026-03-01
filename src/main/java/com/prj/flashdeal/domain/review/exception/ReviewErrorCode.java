package com.prj.flashdeal.domain.review.exception;

import org.springframework.http.HttpStatus;

import com.prj.flashdeal.global.exception.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReviewErrorCode implements ErrorCode {

    ALREADY_REVIEWED("이미 리뷰를 작성한 상품입니다.", HttpStatus.CONFLICT),
    NOT_PURCHASED("구매한 상품에만 리뷰를 작성할 수 있습니다.", HttpStatus.FORBIDDEN);

    private final String message;
    private final HttpStatus status;
}
