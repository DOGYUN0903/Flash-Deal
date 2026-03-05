package com.prj.flashdeal.domain.deal.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prj.flashdeal.domain.deal.dto.request.DealCreateRequest;
import com.prj.flashdeal.domain.deal.dto.request.DealOrderRequest;
import com.prj.flashdeal.domain.deal.dto.response.DealResponse;
import com.prj.flashdeal.domain.deal.entity.Deal;
import com.prj.flashdeal.domain.deal.exception.DealErrorCode;
import com.prj.flashdeal.domain.deal.exception.DealException;
import com.prj.flashdeal.domain.deal.repository.DealRepository;
import com.prj.flashdeal.domain.member.entity.Member;
import com.prj.flashdeal.domain.member.service.MemberService;
import com.prj.flashdeal.domain.order.dto.response.OrderResponse;
import com.prj.flashdeal.domain.order.entity.Order;
import com.prj.flashdeal.domain.order.entity.OrderItem;
import com.prj.flashdeal.domain.order.repository.OrderRepository;
import com.prj.flashdeal.domain.payment.client.TossPaymentClient;
import com.prj.flashdeal.domain.payment.entity.Payment;
import com.prj.flashdeal.domain.payment.entity.PaymentMethod;
import com.prj.flashdeal.domain.product.entity.Product;
import com.prj.flashdeal.domain.product.exception.ProductErrorCode;
import com.prj.flashdeal.domain.product.exception.ProductException;
import com.prj.flashdeal.domain.product.repository.ProductRepository;
import com.prj.flashdeal.domain.stock.service.StockService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DealService {

    private final DealRepository dealRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final MemberService memberService;
    private final StockService stockService;
    private final TossPaymentClient tossPaymentClient;

    // ---------------- 딜 조회 ----------------

    @Transactional(readOnly = true)
    public List<DealResponse> getDeals() {
        return dealRepository.findAll().stream()
            .map(deal -> DealResponse.from(deal, stockService.getStock(deal.getProduct().getId())))
            .toList();
    }

    @Transactional(readOnly = true)
    public DealResponse getDeal(Long dealId) {
        Deal deal = getDealEntity(dealId);
        return DealResponse.from(deal, stockService.getStock(deal.getProduct().getId()));
    }

    @Transactional(readOnly = true)
    public Deal getActiveDeal(Long dealId) {
        Deal deal = getDealEntity(dealId);
        deal.validateActive();
        return deal;
    }

    // ---------------- 딜 주문 ----------------

    /**
     * 선착순 딜 주문
     *
     * V1 문제 재현: @Transactional 안에서 외부 API(Toss) 호출
     * → DB 커넥션을 점유한 채 외부 HTTP 통신 대기 → HikariCP 커넥션 고갈
     */
    @Transactional
    public OrderResponse createDealOrder(Long memberId, Long dealId, DealOrderRequest request) {
        Member member = memberService.getMember(memberId);
        Deal deal = getActiveDeal(dealId);

        if (!request.getAmount().equals(deal.getDiscountPrice())) {
            throw new DealException(DealErrorCode.DEAL_PAYMENT_AMOUNT_MISMATCH);
        }

        // 재고 차감 (SELECT FOR UPDATE)
        stockService.decreaseStock(deal.getProduct().getId(), request.getQuantity());

        // 주문 생성 — 딜 할인가로 OrderItem 생성
        Order order = Order.createOrder(member);
        OrderItem orderItem = OrderItem.createOrderItem(
            deal.getProduct(), request.getQuantity(), deal.getDiscountPrice()
        );
        order.addOrderItem(orderItem);

        // Toss 결제 승인 — @Transactional 안에서 외부 API 호출 (V1 병목 지점)
        tossPaymentClient.confirm(request.getPaymentKey(), request.getOrderId(), request.getAmount());

        // 결제 완료 처리 (Order에 cascade ALL → Payment 자동 저장)
        Payment payment = Payment.builder()
            .order(order)
            .amount(request.getAmount())
            .build();
        payment.completePayment(PaymentMethod.TOSS);
        order.completePayment(payment);

        return OrderResponse.from(orderRepository.save(order));
    }

    // ---------------- 어드민 ----------------

    @Transactional
    public DealResponse createDeal(DealCreateRequest request) {
        Product product = productRepository.findById(request.getProductId())
            .orElseThrow(() -> new ProductException(ProductErrorCode.PRODUCT_NOT_FOUND));

        Deal deal = Deal.builder()
            .product(product)
            .title(request.getTitle())
            .discountPrice(request.getDiscountPrice())
            .startAt(request.getStartAt())
            .endAt(request.getEndAt())
            .build();

        deal.activate();

        Deal saved = dealRepository.save(deal);
        return DealResponse.from(saved, stockService.getStock(product.getId()));
    }

    // ---------------- 헬퍼 ----------------

    private Deal getDealEntity(Long dealId) {
        return dealRepository.findById(dealId)
            .orElseThrow(() -> new DealException(DealErrorCode.DEAL_NOT_FOUND));
    }
}
