package com.prj.flashdeal.domain.product.metrics;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

import com.prj.flashdeal.domain.product.repository.ProductRepository;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProductMetrics {

    private final MeterRegistry meterRegistry;
    private final ProductRepository productRepository;

    private final Set<Long> registered = ConcurrentHashMap.newKeySet();

    /**
     * 재고 Gauge 등록 — Grafana에서 실시간 재고를 시각화하기 위한 메트릭.
     * 상품당 최초 1회만 등록되며, Prometheus scrape 시점마다 DB에서 최신값을 조회한다.
     *
     * [락 없음] 30스레드 동시 요청 시 재고가 0이 아닌 25~29에서 멈춤 → 과매도 시각화
     * [비관적 락] 재고가 정확히 선형 감소 → 정합성 보장 시각화
     */
    @PostConstruct
    public void initGauges() {
        productRepository.findAll()
            .forEach(p -> registerStockGauge(p.getId()));
    }

    public void registerStockGauge(Long productId) {
        if (registered.add(productId)) {
            Gauge.builder("product.stock.remaining", productRepository,
                    repo -> repo.findById(productId)
                        .map(p -> (double) p.getStockQuantity())
                        .orElse(0.0))
                .tag("productId", String.valueOf(productId))
                .description("상품 잔여 재고 (동시성 테스트 시각화용)")
                .register(meterRegistry);
        }
    }
}
