package com.prj.flashdeal.domain.deal.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prj.flashdeal.domain.deal.dto.request.DealOrderRequest;
import com.prj.flashdeal.domain.deal.entity.Deal;
import com.prj.flashdeal.domain.deal.exception.DealErrorCode;
import com.prj.flashdeal.domain.deal.exception.DealException;
import com.prj.flashdeal.domain.deal.repository.DealRepository;
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
import com.prj.flashdeal.domain.stock.service.StockService;

import lombok.RequiredArgsConstructor;

/**
 * 딜 주문의 트랜잭션을 분리하기 위한 서비스.
 *
 * DealService에서 내부 호출 시 @Transactional 프록시가 동작하지 않으므로
 * 별도 빈으로 분리하여 TX 경계를 보장한다.
 */
@Service
@RequiredArgsConstructor
public class DealOrderTransactionService {

    private final DealRepository dealRepository;
    private final MemberService memberService;
    private final StockService stockService;
    private final ProductService productService;
    private final OrderService orderService;

    /**
     * TX1: 검증 + 재고 차감 후 DB 커넥션 즉시 반환
     */
    @Transactional
    public DealOrderContext validateAndDecreaseStock(Long memberId, Long dealId, DealOrderRequest request) {
        Member member = memberService.getMember(memberId);
        Deal deal = findDeal(dealId);
        deal.validateActive();
        deal.validateOrderAmount(request.getAmount(), request.getQuantity());

        stockService.decreaseStock(deal.getProduct().getId(), request.getQuantity());

        return new DealOrderContext(
            member.getId(), deal.getId(), deal.getProduct().getId(),
            deal.getDiscountPrice(), request.getQuantity()
        );
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

    private Deal findDeal(Long dealId) {
        return dealRepository.findById(dealId)
            .orElseThrow(() -> new DealException(DealErrorCode.DEAL_NOT_FOUND));
    }

    public record DealOrderContext(
        Long memberId, Long dealId, Long productId,
        int discountPrice, int quantity
    ) {}
}
