package com.prj.flashdeal.domain.member.entity;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Address {
    private String zipcode;
    private String street;
    private String detail;

    private Address(String zipcode, String street, String detail) {
        this.zipcode = zipcode;
        this.street = street;
        this.detail = detail;
    }

    public static Address of(String zipcode, String street, String detail) {
        return new Address(
            zipcode,
            street,
            detail
        );
    }
}
