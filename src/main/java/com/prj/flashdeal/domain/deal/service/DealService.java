package com.prj.flashdeal.domain.deal.service;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prj.flashdeal.domain.deal.dto.request.DealCreateRequest;
import com.prj.flashdeal.domain.deal.dto.response.DealDetailResponse;
import com.prj.flashdeal.domain.deal.dto.response.DealPurchaseResponse;
import com.prj.flashdeal.domain.deal.dto.response.DealSummaryResponse;
import com.prj.flashdeal.global.response.PageResponse;
import com.prj.flashdeal.domain.deal.entity.Deal;
import com.prj.flashdeal.domain.product.entity.Product;
import com.prj.flashdeal.domain.product.exception.ProductErrorCode;
import com.prj.flashdeal.domain.product.exception.ProductException;
import com.prj.flashdeal.domain.product.repository.ProductRepository;
import com.prj.flashdeal.domain.deal.exception.DealErrorCode;
import com.prj.flashdeal.domain.deal.exception.DealException;
import com.prj.flashdeal.domain.deal.repository.DealRepository;
import com.prj.flashdeal.domain.member.entity.Member;
import com.prj.flashdeal.domain.member.exception.MemberErrorCode;
import com.prj.flashdeal.domain.member.exception.MemberException;
import com.prj.flashdeal.domain.member.repository.MemberRepository;
import com.prj.flashdeal.domain.order.entity.Order;
import com.prj.flashdeal.domain.order.entity.OrderItem;
import com.prj.flashdeal.domain.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class DealService {

    private final DealRepository dealRepository;
    private final ProductRepository productRepository;
    private final MemberRepository memberRepository;
    private final OrderRepository orderRepository;

    @Transactional
    public DealDetailResponse createDeal(DealCreateRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ProductException(ProductErrorCode.PRODUCT_NOT_FOUND));

        Deal deal = Deal.builder()
                .product(product)
                .dealPrice(request.getDealPrice())
                .stock(request.getStock())
                .openTime(request.getOpenTime())
                .endTime(request.getEndTime())
                .build();

        return DealDetailResponse.from(dealRepository.save(deal));
    }

    @Transactional(readOnly = true)
    public PageResponse<DealSummaryResponse> getDeals(Pageable pageable) {
        return new PageResponse<>(
                dealRepository.findAllByDeletedAtIsNull(pageable)
                        .map(DealSummaryResponse::from)
        );
    }

    @Transactional(readOnly = true)
    public DealDetailResponse getDeal(Long dealId) {
        Deal deal = dealRepository.findById(dealId)
                .orElseThrow(() -> new DealException(DealErrorCode.DEAL_NOT_FOUND));
        return DealDetailResponse.from(deal);
    }

    @Transactional
    public DealPurchaseResponse purchase(Long memberId, Long dealId) {
        // 1. 조회 (No Lock - 동시성 이슈 의도적 발생 지점)
        Deal deal = dealRepository.findById(dealId)
                .orElseThrow(() -> new DealException(DealErrorCode.DEAL_NOT_FOUND));

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        // 2. Gate Validation - 오픈 시간 체크
        if (!deal.isOpen()) {
            throw new DealException(DealErrorCode.DEAL_NOT_OPEN);
        }

        // 3. 재고 차감 (No Lock → Race Condition 발생 가능)
        deal.decreaseStock(1);

        // 4. 주문 생성 (PENDING - 토스 결제 완료 후 PAID로 변경됨)
        Order order = Order.builder()
                .member(member)
                .dealId(deal.getId())
                .build();

        OrderItem orderItem = OrderItem.builder()
                .product(deal.getProduct())
                .quantity(1)
                .price(deal.getDealPrice())
                .build();
        order.addOrderItem(orderItem);

        orderRepository.save(order);

        log.info("딜 주문 생성 - memberId: {}, dealId: {}, orderId: {}, remainingStock: {}",
                memberId, dealId, order.getId(), deal.getStock());

        return DealPurchaseResponse.of(order, deal);
    }
}
