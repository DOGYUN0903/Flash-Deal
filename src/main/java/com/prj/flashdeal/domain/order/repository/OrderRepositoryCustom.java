package com.prj.flashdeal.domain.order.repository;

import java.time.LocalDateTime;
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

    /**
     * 만료된 PENDING 주문 조회 (스케줄러용)
     */
    List<Order> findExpiredPendingOrders(LocalDateTime expiredBefore);

    /**
     * 구매 이력 확인 (리뷰 작성 검증용)
     */
    boolean existsPurchasedProduct(Long memberId, Long productId);
}
