package com.prj.flashdeal.domain.stock.service;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import com.prj.flashdeal.domain.stock.exception.StockErrorCode;
import com.prj.flashdeal.domain.stock.exception.StockException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RedisLockService {

    private final RedissonClient redissonClient;

    public <T> T executeWithLock(String lockKey, Duration waitTime, Duration leaseTime, Supplier<T> task) {
        RLock lock = redissonClient.getLock(lockKey);

        try {
            boolean acquired = lock.tryLock(waitTime.toSeconds(), leaseTime.toSeconds(), TimeUnit.SECONDS);
            if (!acquired) {
                throw new StockException(StockErrorCode.STOCK_LOCK_ACQUISITION_FAILED);
            }

            return task.get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new StockException(StockErrorCode.STOCK_LOCK_ACQUISITION_FAILED);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}
