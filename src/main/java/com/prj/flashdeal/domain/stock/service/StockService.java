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
        Stock stock = stockRepository.findByProduct_Id(productId)
            .orElseThrow(() -> new StockException(StockErrorCode.STOCK_NOT_FOUND));

        stock.increase(quantity);
        stock.getProduct().markOnSale();
    }

    /**
     * 현재 재고 조회
     */
    @Transactional(readOnly = true)
    public int getStock(Long productId) {
        return stockRepository.findByProduct_Id(productId)
            .map(Stock::getQuantity)
            .orElse(0);
    }

}
