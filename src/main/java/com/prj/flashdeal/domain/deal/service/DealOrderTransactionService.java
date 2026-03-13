package com.prj.flashdeal.domain.deal.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prj.flashdeal.domain.deal.dto.request.DealOrderRequest;
import com.prj.flashdeal.domain.deal.exception.DealErrorCode;
import com.prj.flashdeal.domain.deal.exception.DealException;
import com.prj.flashdeal.domain.member.entity.Member;
import com.prj.flashdeal.domain.member.service.MemberService;
import com.prj.flashdeal.domain.order.dto.response.OrderResponse;
import com.prj.flashdeal.domain.order.entity.Order;
import com.prj.flashdeal.domain.order.entity.OrderItem;
import com.prj.flashdeal.domain.order.service.OrderService;
import com.prj.flashdeal.domain.payment.entity.Payment;
import com.prj.flashdeal.domain.payment.entity.PaymentMethod;
import com.prj.flashdeal.domain.product.entity.Product;
import com.prj.flashdeal.domain.product.service.ProductService;
import com.prj.flashdeal.domain.stock.service.RedisStockScriptService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DealOrderTransactionService {

    private final RedisStockScriptService redisStockScriptService;
    private final DealOrderStockTransactionService dealOrderStockTxService;
    private final MemberService memberService;
    private final ProductService productService;
    private final OrderService orderService;

    /**
     * TX1: 딜 검증 후 Redis Lua Script로 재고를 예약 차감하고, 별도 트랜잭션에서 DB 재고를 반영한다.
     */
    public DealOrderContext validateAndDecreaseStock(Long memberId, Long dealId, DealOrderRequest request) {
        DealOrderContext context = dealOrderStockTxService.validateOrderContext(memberId, dealId, request);
        boolean reserved = false;

        try {
            redisStockScriptService.decrease(context.productId(), context.quantity());
            reserved = true;
            dealOrderStockTxService.applyReservedStock(context.productId(), context.quantity());
            return context;
        } catch (RuntimeException e) {
            if (reserved) {
                redisStockScriptService.increase(context.productId(), context.quantity());
            }
            throw e;
        }
    }

    public void restoreStock(Long productId, int quantity) {
        redisStockScriptService.increase(productId, quantity);
        dealOrderStockTxService.restoreReservedStock(productId, quantity);
    }

    /**
     * TX2: 주문 생성 + 결제 완료 처리
     */
    @Transactional
    public OrderResponse completeOrder(DealOrderContext context, DealOrderRequest request) {
        Member member = memberService.getMember(context.memberId());
        Product product = productService.findCartableProduct(context.productId());

        Order order = Order.createOrder(member);
        order.addOrderItem(OrderItem.createOrderItem(
            product, context.quantity(), context.discountPrice()
        ));

        Payment payment = Payment.builder()
            .order(order)
            .amount(request.getAmount())
            .build();
        payment.completePayment(PaymentMethod.TOSS);
        order.completePayment(payment);

        return OrderResponse.from(orderService.saveOrder(order));
    }

    static DealException dealNotFound(Long dealId) {
        return new DealException(DealErrorCode.DEAL_NOT_FOUND);
    }

    public record DealOrderContext(
        Long memberId, Long dealId, Long productId,
        int discountPrice, int quantity
    ) {}
}
