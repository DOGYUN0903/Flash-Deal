package com.prj.flashdeal.domain.order.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.prj.flashdeal.domain.member.entity.Member;
import com.prj.flashdeal.domain.order.dto.response.OrderSummaryResponse;
import com.prj.flashdeal.domain.order.entity.Order;

public interface OrderRepositoryCustom {

    Optional<Order> findByIdAndMemberId(Long orderId, Long memberId);

    List<Order> findAllByMemberOrderByCreatedAtDesc(Member member);

    /**
     * 전체 주문 목록 조회 (관리자) - DTO Projection
     */
    Page<OrderSummaryResponse> findAllOrderSummaries(Pageable pageable);

    /**
     * 회원별 주문 목록 조회 - DTO Projection (페이징)
     */
    Page<OrderSummaryResponse> findOrderSummariesByMember(Member member, Pageable pageable);
}
