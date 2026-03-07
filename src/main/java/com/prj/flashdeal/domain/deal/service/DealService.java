package com.prj.flashdeal.domain.deal.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prj.flashdeal.domain.deal.dto.request.DealCreateRequest;
import com.prj.flashdeal.domain.deal.dto.request.DealOrderRequest;
import com.prj.flashdeal.domain.deal.dto.response.DealResponse;
import com.prj.flashdeal.domain.deal.entity.Deal;
import com.prj.flashdeal.domain.deal.exception.DealErrorCode;
import com.prj.flashdeal.domain.deal.exception.DealException;
import com.prj.flashdeal.domain.deal.repository.DealRepository;
import com.prj.flashdeal.global.response.PageResponse;
import com.prj.flashdeal.domain.member.entity.Member;
import com.prj.flashdeal.domain.member.service.MemberService;
import com.prj.flashdeal.domain.order.dto.response.OrderResponse;
import com.prj.flashdeal.domain.order.entity.Order;
import com.prj.flashdeal.domain.order.entity.OrderItem;
import com.prj.flashdeal.domain.order.service.OrderService;
import com.prj.flashdeal.domain.payment.client.FakePaymentClient;
import com.prj.flashdeal.domain.payment.entity.Payment;
import com.prj.flashdeal.domain.payment.entity.PaymentMethod;
import com.prj.flashdeal.domain.product.entity.Product;
import com.prj.flashdeal.domain.product.service.ProductService;
import com.prj.flashdeal.domain.stock.service.StockService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DealService {

    private final DealRepository dealRepository;
    private final OrderService orderService;
    private final ProductService productService;
    private final MemberService memberService;
    private final StockService stockService;
    private final FakePaymentClient fakePaymentClient;

    // ---------------- 딜 조회 ----------------

    @Transactional(readOnly = true)
    public PageResponse<DealResponse> getDeals(Pageable pageable) {
        Page<DealResponse> page = dealRepository.findAll(pageable)
            .map(deal -> DealResponse.from(deal, stockService.getStock(deal.getProduct().getId())));
        return new PageResponse<>(page);
    }

    @Transactional(readOnly = true)
    public DealResponse getDeal(Long dealId) {
        Deal deal = findDeal(dealId);
        return DealResponse.from(deal, stockService.getStock(deal.getProduct().getId()));
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
        Deal deal = findDeal(dealId);
        deal.validateActive();
        deal.validateOrderAmount(request.getAmount(), request.getQuantity());

        // 재고 차감 (SELECT FOR UPDATE)
        stockService.decreaseStock(deal.getProduct().getId(), request.getQuantity());

        // 주문 생성 — 딜 할인가로 OrderItem 생성
        Order order = Order.createOrder(member);
        order.addOrderItem(OrderItem.createOrderItem(
            deal.getProduct(), request.getQuantity(), deal.getDiscountPrice()
        ));

        // V1 병목 지점: @Transactional 안에서 외부 결제 호출 (500~1000ms 지연)
        // → DB 커넥션을 점유한 채 대기 → HikariCP 커넥션 고갈
        fakePaymentClient.pay(member.getId(), request.getAmount());

        // 결제 완료 처리 (Order에 cascade ALL → Payment 자동 저장)
        Payment payment = Payment.builder()
            .order(order)
            .amount(request.getAmount())
            .build();
        payment.completePayment(PaymentMethod.TOSS);
        order.completePayment(payment);

        return OrderResponse.from(orderService.saveOrder(order));
    }

    // ---------------- 어드민 ----------------

    @Transactional
    public DealResponse createDeal(DealCreateRequest request) {
        Product product = productService.findCartableProduct(request.getProductId());

        Deal deal = Deal.builder()
            .product(product)
            .title(request.getTitle())
            .discountPrice(request.getDiscountPrice())
            .startAt(request.getStartAt())
            .endAt(request.getEndAt())
            .build();

        deal.validateTimeRange();
        deal.validateDiscountPrice(product.getPrice());

        Deal saved = dealRepository.save(deal);
        return DealResponse.from(saved, stockService.getStock(product.getId()));
    }

    // ---------------- private 헬퍼 ----------------

    private Deal findDeal(Long dealId) {
        return dealRepository.findById(dealId)
            .orElseThrow(() -> new DealException(DealErrorCode.DEAL_NOT_FOUND));
    }
}
