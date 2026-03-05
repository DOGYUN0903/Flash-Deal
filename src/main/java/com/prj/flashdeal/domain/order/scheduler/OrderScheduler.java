package com.prj.flashdeal.domain.order.scheduler;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.prj.flashdeal.domain.order.entity.Order;
import com.prj.flashdeal.domain.order.entity.OrderItem;
import com.prj.flashdeal.domain.order.repository.OrderRepository;
import com.prj.flashdeal.domain.stock.service.StockService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderScheduler {

    private static final int PAYMENT_TIMEOUT_MINUTES = 10;

    private final OrderRepository orderRepository;
    private final StockService stockService;

    /**
     * 30분 이상 결제 미완료 PENDING 주문 자동 취소 (매 1분마다 실행)
     */
    @Scheduled(fixedDelay = 60_000)
    @Transactional
    public void cancelExpiredPendingOrders() {
        LocalDateTime expiredBefore = LocalDateTime.now().minusMinutes(PAYMENT_TIMEOUT_MINUTES);
        List<Order> expiredOrders = orderRepository.findExpiredPendingOrders(expiredBefore);

        if (expiredOrders.isEmpty()) {
            return;
        }

        log.info("[OrderScheduler] 만료된 PENDING 주문 {}건 자동 취소 시작", expiredOrders.size());

        for (Order order : expiredOrders) {
            for (OrderItem item : order.getOrderItems()) {
                stockService.increaseStock(item.getProductId(), item.getQuantity());
            }
            order.cancel();
        }

        log.info("[OrderScheduler] 만료된 PENDING 주문 {}건 자동 취소 완료", expiredOrders.size());
    }
}
