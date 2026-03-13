package com.prj.flashdeal.domain.deal.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prj.flashdeal.domain.deal.dto.request.DealOrderRequest;
import com.prj.flashdeal.domain.deal.entity.Deal;
import com.prj.flashdeal.domain.deal.repository.DealRepository;
import com.prj.flashdeal.domain.member.entity.Member;
import com.prj.flashdeal.domain.member.service.MemberService;
import com.prj.flashdeal.domain.stock.service.StockService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DealOrderStockTransactionService {

    private final DealRepository dealRepository;
    private final MemberService memberService;
    private final StockService stockService;

    @Transactional(readOnly = true)
    public DealOrderTransactionService.DealOrderContext validateOrderContext(
        Long memberId,
        Long dealId,
        DealOrderRequest request
    ) {
        Member member = memberService.getMember(memberId);
        Deal deal = dealRepository.findById(dealId)
            .orElseThrow(() -> DealOrderTransactionService.dealNotFound(dealId));

        deal.validateActive();
        deal.validateOrderAmount(request.getAmount(), request.getQuantity());

        return new DealOrderTransactionService.DealOrderContext(
            member.getId(),
            deal.getId(),
            deal.getProduct().getId(),
            deal.getDiscountPrice(),
            request.getQuantity()
        );
    }

    @Transactional
    public void applyReservedStock(Long productId, int quantity) {
        stockService.decreaseReservedStock(productId, quantity);
    }

    @Transactional
    public void restoreReservedStock(Long productId, int quantity) {
        stockService.increaseReservedStock(productId, quantity);
    }
}
