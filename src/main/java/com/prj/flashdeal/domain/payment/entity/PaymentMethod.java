package com.prj.flashdeal.domain.payment.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PaymentMethod {
    CARD("카드"),
    CASH("현금"),
    TRANSFER("계좌이체");

    private final String description;
}
