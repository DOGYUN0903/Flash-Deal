package com.prj.flashdeal.domain.stock.metrics;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

import com.prj.flashdeal.domain.stock.repository.StockRepository;
import com.prj.flashdeal.domain.stock.service.StockService;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class StockMetrics {

    private final MeterRegistry meterRegistry;
    private final StockRepository stockRepository;
    private final StockService stockService;

    private final Set<Long> registered = ConcurrentHashMap.newKeySet();

    /**
     * 재고 Gauge 등록 — Grafana에서 실시간 재고를 시각화하기 위한 메트릭.
     * 상품당 최초 1회만 등록되며, Prometheus scrape 시점마다 StockService에서 최신값을 조회한다.
     *
     * [락 없음] 동시 요청 시 재고가 정확한 값이 아닌 값에서 멈춤 → 과매도 시각화
     * [비관적 락] 재고가 정확히 선형 감소 → 정합성 보장 시각화
     */
    @PostConstruct
    public void initGauges() {
        stockRepository.findAll()
            .forEach(stock -> registerStockGauge(stock.getProduct().getId()));
    }

    public void registerStockGauge(Long productId) {
        if (registered.add(productId)) {
            Gauge.builder("product.stock.remaining", stockService,
                    svc -> (double) svc.getStock(productId))
                .tag("productId", String.valueOf(productId))
                .description("상품 잔여 재고 (동시성 테스트 시각화용)")
                .register(meterRegistry);
        }
    }
}
