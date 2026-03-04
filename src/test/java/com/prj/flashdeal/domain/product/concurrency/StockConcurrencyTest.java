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
import com.prj.flashdeal.domain.product.service.ProductService;

@SpringBootTest
@DisplayName("재고 동시성 통합 테스트")
class StockConcurrencyTest {

    @Autowired private ProductService productService;
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

        Product product = Product.builder()
            .name("동시성 테스트 상품")
            .description("테스트용 상품")
            .price(10000)
            .stock(INITIAL_STOCK)
            .category(ProductCategory.ELECTRONICS)
            .build();

        productId = productRepository.save(product).getId();
    }

    @AfterEach
    void tearDown() {
        productRepository.deleteById(productId);
    }

    @Test
    @DisplayName("낙관적 락 - 고경합 환경에서 OptimisticLockException 폭증")
    void optimisticLock_고경합시_예외폭증() throws InterruptedException {
        // given
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
        CountDownLatch latch = new CountDownLatch(THREAD_COUNT);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        // when - findById() 사용 (@Version 낙관적 락, 커밋 시점에 충돌 감지)
        for (int i = 0; i < THREAD_COUNT; i++) {
            executor.submit(() -> {
                try {
                    txTemplate.execute(status -> {
                        Product product = productRepository.findById(productId).orElseThrow();
                        product.decreaseStock(1);
                        return null;
                    });
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    // OptimisticLockException 또는 재고 부족 예외
                    failCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        Product result = productRepository.findById(productId).orElseThrow();
        System.out.printf("[낙관적 락] 성공: %d건, 실패: %d건, 최종 재고: %d%n",
            successCount.get(), failCount.get(), result.getStockQuantity());

        // then - 고경합 시 대부분의 요청이 OptimisticLockException으로 실패
        assertThat(failCount.get()).isGreaterThan(0);
    }

    @Test
    @DisplayName("비관적 락 - 동시 주문 시 재고 정합성 보장")
    void pessimisticLock_동시주문시_재고정합성_보장() throws InterruptedException {
        // given
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
        CountDownLatch latch = new CountDownLatch(THREAD_COUNT);
        AtomicInteger successCount = new AtomicInteger(0);

        // when - productService.decreaseStock() 사용 (SELECT FOR UPDATE 비관적 락)
        for (int i = 0; i < THREAD_COUNT; i++) {
            executor.submit(() -> {
                try {
                    productService.decreaseStock(productId, 1);
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

        Product result = productRepository.findById(productId).orElseThrow();
        System.out.printf("[비관적 락] 성공: %d건, 최종 재고: %d%n",
            successCount.get(), result.getStockQuantity());

        // then - 성공한 만큼 정확히 재고가 차감됨
        assertThat(result.getStockQuantity()).isEqualTo(INITIAL_STOCK - successCount.get());
    }
}
