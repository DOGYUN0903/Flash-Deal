package com.prj.flashdeal.domain.deal.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.prj.flashdeal.domain.deal.dto.cache.DealDetailCacheValue;
import com.prj.flashdeal.domain.deal.dto.cache.DealListItemCacheValue;
import com.prj.flashdeal.domain.deal.dto.response.DealResponse;

public interface DealRepositoryCustom {

    Page<DealResponse> findDealsWithStock(Pageable pageable);

    Page<DealListItemCacheValue> findDealListCacheValues(Pageable pageable);

    DealResponse findDealWithStock(Long dealId);

    DealDetailCacheValue findDealDetailCacheValue(Long dealId);
}
