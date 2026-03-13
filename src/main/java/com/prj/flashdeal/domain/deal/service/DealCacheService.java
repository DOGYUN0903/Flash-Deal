package com.prj.flashdeal.domain.deal.service;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prj.flashdeal.domain.deal.dto.cache.DealDetailCacheValue;
import com.prj.flashdeal.domain.deal.dto.cache.DealListPageCacheValue;
import com.prj.flashdeal.domain.deal.exception.DealErrorCode;
import com.prj.flashdeal.domain.deal.exception.DealException;
import com.prj.flashdeal.domain.deal.repository.DealRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DealCacheService {

    private final DealRepository dealRepository;
    private final CacheManager cacheManager;

    @Cacheable(value = "deal", key = "#dealId")
    @Transactional(readOnly = true)
    public DealDetailCacheValue getDealDetailMetadata(Long dealId) {
        DealDetailCacheValue response = dealRepository.findDealDetailCacheValue(dealId);
        if (response == null) {
            throw new DealException(DealErrorCode.DEAL_NOT_FOUND);
        }
        return response;
    }

    @Cacheable(value = "deals", key = "#page + ':' + #size")
    @Transactional(readOnly = true)
    public DealListPageCacheValue getDealListMetadata(int page, int size) {
        PageRequest pageable = PageRequest.of(page, size);
        Page<com.prj.flashdeal.domain.deal.dto.cache.DealListItemCacheValue> result =
            dealRepository.findDealListCacheValues(pageable);

        return new DealListPageCacheValue(
            result.getContent(),
            result.getNumber(),
            result.getSize(),
            result.getTotalElements(),
            result.getTotalPages()
        );
    }

    public void evictDealListMetadata(int page, int size) {
        Cache cache = cacheManager.getCache("deals");
        if (cache != null) {
            cache.evict(page + ":" + size);
        }
    }
}
