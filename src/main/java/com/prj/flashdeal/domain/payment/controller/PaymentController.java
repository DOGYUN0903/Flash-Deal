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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.prj.flashdeal.domain.payment.dto.request.PaymentRequest;
import com.prj.flashdeal.domain.payment.dto.request.TossConfirmRequest;
import com.prj.flashdeal.domain.payment.dto.response.PaymentResponse;
import com.prj.flashdeal.domain.payment.service.PaymentService;
import com.prj.flashdeal.global.response.ApiResponse;
import com.prj.flashdeal.global.security.CustomUserDetails;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Payment", description = "결제 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    /**
     * 결제 처리
     */
    @Operation(summary = "결제 처리", description = "주문에 대한 결제를 처리합니다. 동일 주문에 대한 중복 요청 시 기존 결제를 반환합니다.")
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
     * 토스페이먼츠 결제 승인
     */
    @Operation(summary = "토스페이먼츠 결제 승인", description = "토스페이먼츠 결제 승인을 요청합니다. 동일 주문에 대한 중복 요청 시 기존 결제를 반환합니다.")
    @PostMapping("/toss/confirm")
    public ResponseEntity<ApiResponse<PaymentResponse>> confirmTossPayment(
        @AuthenticationPrincipal CustomUserDetails userPrincipal,
        @Valid @RequestBody TossConfirmRequest request
    ) {
        return ApiResponse.success(
            HttpStatus.OK,
            "결제가 승인되었습니다.",
            paymentService.confirmTossPayment(userPrincipal.getUserId(), request)
        );
    }

    /**
     * 결제 조회
     */
    @Operation(summary = "결제 단건 조회", description = "결제 ID로 결제 정보를 조회합니다.")
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
    @Operation(summary = "주문 ID로 결제 조회", description = "주문 ID로 해당 주문의 결제 정보를 조회합니다.")
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
    @Operation(summary = "환불 처리", description = "결제를 환불 처리합니다. 연결된 주문도 함께 취소됩니다.")
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
