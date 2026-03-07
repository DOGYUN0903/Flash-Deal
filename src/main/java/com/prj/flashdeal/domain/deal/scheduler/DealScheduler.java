package com.prj.flashdeal.domain.deal.scheduler;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.prj.flashdeal.domain.deal.entity.Deal;
import com.prj.flashdeal.domain.deal.entity.DealStatus;
import com.prj.flashdeal.domain.deal.repository.DealRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "scheduling.enabled", havingValue = "true", matchIfMissing = true)
public class DealScheduler {

    private final DealRepository dealRepository;

    @Scheduled(fixedDelay = 10_000)
    @Transactional
    public void updateDealStatuses() {
        LocalDateTime now = LocalDateTime.now();

        List<Deal> scheduledDeals = dealRepository.findByStatus(DealStatus.SCHEDULED);
        for (Deal deal : scheduledDeals) {
            if (deal.shouldActivate(now)) {
                deal.activate();
                log.info("[DealScheduler] 딜 활성화 - dealId: {}, title: {}", deal.getId(), deal.getTitle());
            }
        }

        List<Deal> activeDeals = dealRepository.findByStatus(DealStatus.ACTIVE);
        for (Deal deal : activeDeals) {
            if (deal.shouldEnd(now)) {
                deal.end();
                log.info("[DealScheduler] 딜 종료 - dealId: {}, title: {}", deal.getId(), deal.getTitle());
            }
        }
    }
}
