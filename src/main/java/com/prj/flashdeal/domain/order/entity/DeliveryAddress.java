package com.prj.flashdeal.domain.order.entity;

import com.prj.flashdeal.domain.member.entity.Member;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DeliveryAddress {

    private String recipientName;
    private String phoneNumber;
    private String zipcode;
    private String street;
    private String detail;

    private DeliveryAddress(
        String recipientName,
        String phoneNumber,
        String zipcode,
        String street,
        String detail
    ) {
        this.recipientName = recipientName;
        this.phoneNumber = phoneNumber;
        this.zipcode = zipcode;
        this.street = street;
        this.detail = detail;
    }

    public static DeliveryAddress from(Member member) {
        return new DeliveryAddress(
            member.getName(),
            member.getPhoneNumber(),
            member.getAddress().getZipcode(),
            member.getAddress().getStreet(),
            member.getAddress().getDetail()
        );
    }

    public static DeliveryAddress of(
        String recipientName,
        String phoneNumber,
        String zipcode,
        String street,
        String detail
    ) {
        return new DeliveryAddress(
            recipientName,
            phoneNumber,
            zipcode,
            street,
            detail
        );
    }
}