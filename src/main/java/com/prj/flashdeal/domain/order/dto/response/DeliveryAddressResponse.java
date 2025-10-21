package com.prj.flashdeal.domain.order.dto.response;

import com.prj.flashdeal.domain.order.entity.DeliveryAddress;

public record DeliveryAddressResponse(
    String recipientName,
    String phoneNumber,
    String zipcode,
    String street,
    String detail
) {
    public static DeliveryAddressResponse from(DeliveryAddress deliveryAddress) {
        return new DeliveryAddressResponse(
            deliveryAddress.getRecipientName(),
            deliveryAddress.getPhoneNumber(),
            deliveryAddress.getZipcode(),
            deliveryAddress.getStreet(),
            deliveryAddress.getDetail()
        );
    }
}
