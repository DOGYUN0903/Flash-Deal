package com.prj.flashdeal.domain.deal.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prj.flashdeal.domain.deal.dto.response.DealPurchaseResponse;
import com.prj.flashdeal.domain.deal.entity.Deal;
import com.prj.flashdeal.domain.deal.exception.DealErrorCode;
import com.prj.flashdeal.domain.deal.exception.DealException;
import com.prj.flashdeal.domain.deal.repository.DealRepository;
import com.prj.flashdeal.domain.member.entity.Member;
import com.prj.flashdeal.domain.member.exception.MemberErrorCode;
import com.prj.flashdeal.domain.member.exception.MemberException;
import com.prj.flashdeal.domain.member.repository.MemberRepository;
import com.prj.flashdeal.domain.order.entity.Order;
import com.prj.flashdeal.domain.order.entity.OrderItem;
import com.prj.flashdeal.domain.order.repository.OrderRepository;
import com.prj.flashdeal.domain.payment.client.FakePaymentClient;
import com.prj.flashdeal.domain.payment.entity.Payment;
import com.prj.flashdeal.domain.payment.entity.PaymentMethod;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class DealService {

    private final DealRepository dealRepository;
    private final MemberRepository memberRepository;
    private final OrderRepository orderRepository;
    private final FakePaymentClient fakePaymentClient;

    @Transactional
    public DealPurchaseResponse purchase(Long memberId, Long dealId) {
        // 1. 조회 (No Lock - 동시성 이슈 의도적 발생 지점)
        Deal deal = dealRepository.findById(dealId)
                .orElseThrow(() -> new DealException(DealErrorCode.DEAL_NOT_FOUND));

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        // 2. Gate Validation - 오픈 시간 체크
        if (!deal.isOpen()) {
            throw new DealException(DealErrorCode.DEAL_NOT_OPEN);
        }

        // 3. 재고 검증 & 차감 (No Lock → Race Condition 발생 가능)
        deal.decreaseStock(1);

        // 4. Mock 결제 (500~1000ms 지연, 20% 실패)
        //    DB Lock을 점유한 상태가 아니므로 다른 스레드가 동시 접근 가능
        fakePaymentClient.pay(memberId, deal.getDealPrice());

        // 5. 주문 생성
        Order order = Order.builder()
                .member(member)
                .dealId(deal.getId())
                .build();

        OrderItem orderItem = OrderItem.builder()
                .product(deal.getProduct())
                .quantity(1)
                .price(deal.getDealPrice())
                .build();
        order.addOrderItem(orderItem);

        Payment payment = Payment.builder()
                .order(order)
                .amount(deal.getDealPrice())
                .build();
        payment.completePayment(PaymentMethod.TRANSFER);
        order.completePayment(payment);

        orderRepository.save(order);

        // 6. 포인트 차감
        member.use(deal.getDealPrice().longValue());

        log.info("딜 구매 완료 - memberId: {}, dealId: {}, remainingStock: {}",
                memberId, dealId, deal.getStock());

        return DealPurchaseResponse.of(order, deal);
    }
}
