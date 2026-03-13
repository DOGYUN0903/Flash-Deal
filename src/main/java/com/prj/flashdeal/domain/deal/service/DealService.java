package com.prj.flashdeal.domain.deal.service;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prj.flashdeal.domain.deal.dto.cache.DealDetailCacheValue;
import com.prj.flashdeal.domain.deal.dto.cache.DealListPageCacheValue;
import com.prj.flashdeal.domain.deal.dto.request.DealCreateRequest;
import com.prj.flashdeal.domain.deal.dto.request.DealOrderRequest;
import com.prj.flashdeal.domain.deal.dto.response.DealResponse;
import com.prj.flashdeal.domain.deal.entity.Deal;
import com.prj.flashdeal.domain.deal.repository.DealRepository;
import com.prj.flashdeal.domain.deal.service.DealOrderTransactionService.DealOrderContext;
import com.prj.flashdeal.domain.order.dto.response.OrderResponse;
import com.prj.flashdeal.domain.payment.client.FakePaymentClient;
import com.prj.flashdeal.domain.product.entity.Product;
import com.prj.flashdeal.domain.product.service.ProductService;
import com.prj.flashdeal.domain.stock.service.StockService;
import com.prj.flashdeal.global.response.PageResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DealService {

    private final DealRepository dealRepository;
    private final ProductService productService;
    private final StockService stockService;
    private final FakePaymentClient fakePaymentClient;
    private final DealOrderTransactionService dealOrderTxService;
    private final DealCacheService dealCacheService;

    @Transactional(readOnly = true)
    public PageResponse<DealResponse> getDeals(Pageable pageable) {
        DealListPageCacheValue metadataPage =
            dealCacheService.getDealListMetadata(pageable.getPageNumber(), pageable.getPageSize());

        List<Long> productIds = metadataPage.content().stream()
            .map(item -> item.productId())
            .toList();

        Map<Long, Integer> stockByProductId = stockService.getStocks(productIds);
        return metadataPage.toResponse(stockByProductId);
    }

    @Transactional(readOnly = true)
    public DealResponse getDeal(Long dealId) {
        DealDetailCacheValue metadata = dealCacheService.getDealDetailMetadata(dealId);
        int remainingStock = stockService.getStock(metadata.productId());
        return metadata.toResponse(remainingStock);
    }

    public OrderResponse createDealOrder(Long memberId, Long dealId, DealOrderRequest request) {
        DealOrderContext context = dealOrderTxService.validateAndDecreaseStock(memberId, dealId, request);

        try {
            fakePaymentClient.pay(context.memberId(), request.getAmount());
        } catch (Exception e) {
            dealOrderTxService.restoreStockWithRedisLock(context.dealId(), context.productId(), request.getQuantity());
            throw e;
        }

        return dealOrderTxService.completeOrder(context, request);
    }

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
        dealCacheService.evictDealListMetadata(0, 10);
        return DealResponse.from(saved, stockService.getStock(product.getId()));
    }
}
