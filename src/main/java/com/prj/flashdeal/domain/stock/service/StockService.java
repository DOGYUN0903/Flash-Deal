package com.prj.flashdeal.domain.stock.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prj.flashdeal.domain.product.entity.Product;
import com.prj.flashdeal.domain.stock.entity.Stock;
import com.prj.flashdeal.domain.stock.exception.StockErrorCode;
import com.prj.flashdeal.domain.stock.exception.StockException;
import com.prj.flashdeal.domain.stock.repository.StockRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StockService {

    private final StockRepository stockRepository;

    /**
     * 재고 생성 — 상품 등록 시 호출
     */
    @Transactional
    public void createStock(Product product, int quantity) {
        Stock stock = Stock.builder()
            .product(product)
            .quantity(quantity)
            .build();
        stockRepository.save(stock);
    }

    /**
     * 재고 차감 — SELECT FOR UPDATE 후 차감.
     * 재고가 0이 되면 상품을 SOLD_OUT으로 변경.
     */
    @Transactional
    public void decreaseStock(Long productId, int quantity) {
        Stock stock = stockRepository.findByProductIdWithLock(productId)
            .orElseThrow(() -> new StockException(StockErrorCode.STOCK_NOT_FOUND));

        stock.decrease(quantity);
        // TODO: V3에서 StockDepletedEvent로 개선 예정
        // ProductService 순환 의존 문제로 StockService에서 Product 상태 직접 변경
        if (stock.getQuantity() == 0) {
            stock.getProduct().markSoldOut();
        }
    }

    /**
     * 재고 복구 — 주문 취소 시 호출.
     * 재고가 다시 생기면 상품을 ON_SALE로 변경.
     */
    @Transactional
    public void increaseStock(Long productId, int quantity) {
        Stock stock = stockRepository.findByProductId(productId)
            .orElseThrow(() -> new StockException(StockErrorCode.STOCK_NOT_FOUND));

        stock.increase(quantity);
        // TODO: V3에서 StockRestoredEvent로 개선 예정
        stock.getProduct().markOnSale();
    }

    /**
     * 재고 직접 설정 — 어드민 상품 수정 시 호출.
     * 재고가 0이면 SOLD_OUT, 0보다 크면 ON_SALE로 상품 상태 변경.
     */
    @Transactional
    public void updateStock(Long productId, int quantity) {
        Stock stock = stockRepository.findByProductId(productId)
            .orElseThrow(() -> new StockException(StockErrorCode.STOCK_NOT_FOUND));

        stock.updateQuantity(quantity);
        // TODO: V3에서 Domain Event로 개선 예정
        if (quantity == 0) {
            stock.getProduct().markSoldOut();
        } else {
            stock.getProduct().markOnSale();
        }
    }

    /**
     * 현재 재고 조회
     */
    @Transactional(readOnly = true)
    public int getStock(Long productId) {
        return stockRepository.findByProductId(productId)
            .map(Stock::getQuantity)
            .orElse(0);
    }

}
