package com.prj.flashdeal.domain.payment.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.prj.flashdeal.domain.payment.dto.request.PaymentRequest;
import com.prj.flashdeal.domain.payment.dto.response.PaymentResponse;
import com.prj.flashdeal.domain.payment.service.PaymentService;
import com.prj.flashdeal.global.response.ApiResponse;
import com.prj.flashdeal.global.security.CustomUserDetails;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    /**
     * 결제 처리
     */
    @PostMapping
    public ResponseEntity<ApiResponse<PaymentResponse>> processPayment(
        @AuthenticationPrincipal CustomUserDetails userPrincipal,
        @Valid @RequestBody PaymentRequest request
    ) {
        return ApiResponse.success(
            HttpStatus.CREATED,
            "결제가 완료되었습니다.",
            paymentService.processPayment(userPrincipal.getUserId(), request)
        );
    }

    /**
     * 결제 조회
     */
    @GetMapping("/{paymentId}")
    public ResponseEntity<ApiResponse<PaymentResponse>> getPayment(
        @AuthenticationPrincipal CustomUserDetails userPrincipal,
        @PathVariable Long paymentId
    ) {
        return ApiResponse.success(
            HttpStatus.OK,
            "결제 정보 조회가 완료되었습니다.",
            paymentService.getPayment(userPrincipal.getUserId(), paymentId)
        );
    }

    /**
     * 주문 ID로 결제 조회
     */
    @GetMapping
    public ResponseEntity<ApiResponse<PaymentResponse>> getPaymentByOrderId(
        @AuthenticationPrincipal CustomUserDetails userPrincipal,
        @RequestParam Long orderId
    ) {
        return ApiResponse.success(
            HttpStatus.OK,
            "결제 정보 조회가 완료되었습니다.",
            paymentService.getPaymentByOrderId(userPrincipal.getUserId(), orderId)
        );
    }

    /**
     * 환불 처리
     */
    @PostMapping("/{paymentId}/refund")
    public ResponseEntity<ApiResponse<Void>> refundPayment(
        @AuthenticationPrincipal CustomUserDetails userPrincipal,
        @PathVariable Long paymentId
    ) {
        paymentService.refundPayment(userPrincipal.getUserId(), paymentId);
        return ApiResponse.success(
            HttpStatus.OK,
            "환불이 완료되었습니다.",
            null
        );
    }
}
