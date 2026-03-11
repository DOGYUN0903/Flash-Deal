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
import com.prj.flashdeal.domain.deal.service.DealOrderTransactionService.DealOrderContext;
import com.prj.flashdeal.global.response.PageResponse;
import com.prj.flashdeal.domain.order.dto.response.OrderResponse;
import com.prj.flashdeal.domain.payment.client.FakePaymentClient;
import com.prj.flashdeal.domain.product.entity.Product;
import com.prj.flashdeal.domain.product.service.ProductService;
import com.prj.flashdeal.domain.stock.service.StockService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DealService {

    private final DealRepository dealRepository;
    private final ProductService productService;
    private final StockService stockService;
    private final FakePaymentClient fakePaymentClient;
    private final DealOrderTransactionService dealOrderTxService;

    // ---------------- 딜 조회 ----------------

    @Transactional(readOnly = true)
    public PageResponse<DealResponse> getDeals(Pageable pageable) {
        Page<DealResponse> page = dealRepository.findAll(pageable)
            .map(deal -> DealResponse.from(deal, stockService.getStock(deal.getProduct().getId())));
        return new PageResponse<>(page);
    }

    @Transactional(readOnly = true)
    public DealResponse getDeal(Long dealId) {
        DealResponse response = dealRepository.findDealWithStock(dealId);
        if (response == null) {
            throw new DealException(DealErrorCode.DEAL_NOT_FOUND);
        }
        return response;
    }

    // ---------------- 딜 주문 ----------------

    /**
     * 선착순 딜 주문 — 트랜잭션 분리 적용
     *
     * TX1: 검증 + 재고 차감 (DB 커넥션 사용 후 즉시 반환)
     * 결제: 트랜잭션 밖에서 실행 (DB 커넥션 미점유)
     * TX2: 주문 생성 + 결제 완료 처리
     */
    public OrderResponse createDealOrder(Long memberId, Long dealId, DealOrderRequest request) {
        // TX1: 검증 + 재고 차감 → 커넥션 즉시 반환
        DealOrderContext context = dealOrderTxService.validateAndDecreaseStock(memberId, dealId, request);

        // 결제: 트랜잭션 밖 → DB 커넥션 미점유
        try {
            fakePaymentClient.pay(context.memberId(), request.getAmount());
        } catch (Exception e) {
            // 결제 실패 시 재고 복구
            stockService.increaseStock(context.productId(), request.getQuantity());
            throw e;
        }

        // TX2: 주문 생성 + 결제 완료
        return dealOrderTxService.completeOrder(context, request);
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
