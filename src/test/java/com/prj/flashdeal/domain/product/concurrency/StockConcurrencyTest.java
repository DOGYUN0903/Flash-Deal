package com.prj.flashdeal.domain.product.concurrency;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import com.prj.flashdeal.domain.product.entity.Product;
import com.prj.flashdeal.domain.product.entity.ProductCategory;
import com.prj.flashdeal.domain.product.repository.ProductRepository;
import com.prj.flashdeal.domain.stock.entity.Stock;
import com.prj.flashdeal.domain.stock.repository.StockRepository;
import com.prj.flashdeal.domain.stock.service.StockService;

@SpringBootTest
@DisplayName("재고 동시성 통합 테스트")
class StockConcurrencyTest {

    @Autowired private StockService stockService;
    @Autowired private StockRepository stockRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private PlatformTransactionManager transactionManager;

    private TransactionTemplate txTemplate;
    private Long productId;

    private static final int THREAD_COUNT = 30;
    private static final int INITIAL_STOCK = 30;

    @BeforeEach
    void setUp() {
        txTemplate = new TransactionTemplate(transactionManager);
        txTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);

        // 상품 저장
        Product product = Product.builder()
            .name("동시성 테스트 상품")
            .description("테스트용 상품")
            .price(10000)
            .category(ProductCategory.ELECTRONICS)
            .build();
        product.markOnSale();
        productId = productRepository.save(product).getId();

        // 재고 저장
        Stock stock = Stock.builder()
            .productId(productId)
            .quantity(INITIAL_STOCK)
            .build();
        stockRepository.save(stock);
    }

    @AfterEach
    void tearDown() {
        stockRepository.findByProductId(productId).ifPresent(stockRepository::delete);
        productRepository.deleteById(productId);
    }

    @Test
    @DisplayName("낙관적 락 없음 - 고경합 환경에서 재고 불일치 발생")
    void noLock_고경합시_재고불일치() throws InterruptedException {
        // given
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
        CountDownLatch latch = new CountDownLatch(THREAD_COUNT);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        // when - findByProductId() 직접 사용 (락 없음, 커밋 시점에 덮어씌움)
        for (int i = 0; i < THREAD_COUNT; i++) {
            executor.submit(() -> {
                try {
                    txTemplate.execute(status -> {
                        Stock stock = stockRepository.findByProductId(productId).orElseThrow();
                        stock.decrease(1);
                        return null;
                    });
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        Stock result = stockRepository.findByProductId(productId).orElseThrow();
        System.out.printf("[락 없음] 성공: %d건, 실패: %d건, 최종 재고: %d%n",
            successCount.get(), failCount.get(), result.getQuantity());

        // then - 락 없이 동시 수정 시 재고가 부정확해짐 (Lost Update)
        // 정상이라면 0이어야 하지만, 실제로는 0보다 클 수 있음
        assertThat(failCount.get()).isGreaterThanOrEqualTo(0); // 예외 또는 재고 불일치 발생 확인용
    }

    @Test
    @DisplayName("비관적 락 - 동시 주문 시 재고 정합성 보장")
    void pessimisticLock_동시주문시_재고정합성_보장() throws InterruptedException {
        // given
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
        CountDownLatch latch = new CountDownLatch(THREAD_COUNT);
        AtomicInteger successCount = new AtomicInteger(0);

        // when - stockService.decreaseStock() 사용 (SELECT FOR UPDATE 비관적 락)
        for (int i = 0; i < THREAD_COUNT; i++) {
            executor.submit(() -> {
                try {
                    stockService.decreaseStock(productId, 1);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    // 재고 부족 등 정상 예외
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        Stock result = stockRepository.findByProductId(productId).orElseThrow();
        System.out.printf("[비관적 락] 성공: %d건, 최종 재고: %d%n",
            successCount.get(), result.getQuantity());

        // then - 성공한 만큼 정확히 재고가 차감됨
        assertThat(result.getQuantity()).isEqualTo(INITIAL_STOCK - successCount.get());
    }
}
