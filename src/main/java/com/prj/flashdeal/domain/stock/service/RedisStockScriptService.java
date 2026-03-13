package com.prj.flashdeal.domain.stock.service;

import java.util.Collections;

import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import com.prj.flashdeal.domain.stock.exception.StockErrorCode;
import com.prj.flashdeal.domain.stock.exception.StockException;
import com.prj.flashdeal.domain.stock.repository.StockRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RedisStockScriptService {

    private static final long STOCK_KEY_NOT_FOUND = -1L;
    private static final long OUT_OF_STOCK = -2L;

    private final StringRedisTemplate stringRedisTemplate;
    private final StockRepository stockRepository;

    private final DefaultRedisScript<Long> decreaseScript = createScript("lua/decrease_stock.lua");
    private final DefaultRedisScript<Long> increaseScript = createScript("lua/increase_stock.lua");

    public long decrease(Long productId, int quantity) {
        String key = buildStockKey(productId);
        ensureStockInitialized(productId, key);

        Long result = stringRedisTemplate.execute(
            decreaseScript,
            Collections.singletonList(key),
            String.valueOf(quantity)
        );

        if (result == null || result == STOCK_KEY_NOT_FOUND) {
            throw new StockException(StockErrorCode.STOCK_NOT_FOUND);
        }
        if (result == OUT_OF_STOCK) {
            throw new StockException(StockErrorCode.OUT_OF_STOCK);
        }

        return result;
    }

    public long increase(Long productId, int quantity) {
        String key = buildStockKey(productId);
        ensureStockInitialized(productId, key);

        Long result = stringRedisTemplate.execute(
            increaseScript,
            Collections.singletonList(key),
            String.valueOf(quantity)
        );

        if (result == null || result == STOCK_KEY_NOT_FOUND) {
            throw new StockException(StockErrorCode.STOCK_NOT_FOUND);
        }

        return result;
    }

    private void ensureStockInitialized(Long productId, String key) {
        stockRepository.findByProductId(productId)
            .ifPresentOrElse(
                stock -> stringRedisTemplate.opsForValue()
                    .setIfAbsent(key, String.valueOf(stock.getQuantity())),
                () -> {
                    throw new StockException(StockErrorCode.STOCK_NOT_FOUND);
                }
            );
    }

    private String buildStockKey(Long productId) {
        return "stock:" + productId;
    }

    private DefaultRedisScript<Long> createScript(String path) {
        DefaultRedisScript<Long> script = new DefaultRedisScript<>();
        script.setLocation(new ClassPathResource(path));
        script.setResultType(Long.class);
        return script;
    }
}
