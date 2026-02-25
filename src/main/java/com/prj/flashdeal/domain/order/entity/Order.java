package com.prj.flashdeal.domain.order.entity;

import java.util.ArrayList;
import java.util.List;

import com.prj.flashdeal.domain.member.entity.Member;
import com.prj.flashdeal.domain.order.exception.OrderErrorCode;
import com.prj.flashdeal.domain.order.exception.OrderException;
import com.prj.flashdeal.domain.payment.entity.Payment;
import com.prj.flashdeal.global.entity.BaseTimeEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "orders")
public class Order extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private Payment payment;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Column(nullable = false)
    private Integer totalPrice;

    @Builder
    private Order(Member member) {
        this.member = member;
        this.status = OrderStatus.PENDING;
        this.totalPrice = 0;
    }

    /**
     * 주문 생성 정적 팩토리 메서드
     */
    public static Order createOrder(Member member) {
        return Order.builder()
                .member(member)
                .build();
    }

    /**
     * 주문 항목 추가 (양방향 연관관계 편의 메서드)
     */
    public void addOrderItem(OrderItem orderItem) {
        this.orderItems.add(orderItem);
        orderItem.setOrder(this);
        this.totalPrice += orderItem.calculateTotalPrice();
    }

    /**
     * 주문 취소 (상태만 변경, 재고 복구는 Service에서 처리)
     */
    public void cancel() {
        if (this.status == OrderStatus.DELIVERED) {
            throw new OrderException(OrderErrorCode.CANNOT_CANCEL_DELIVERED_ORDER);
        }

        this.status = OrderStatus.CANCELED;
    }

    /**
     * 결제 완료 처리 및 Payment 연관관계 설정 (양방향 연관관계 편의 메서드)
     */
    public void completePayment(Payment payment) {
        if (this.status != OrderStatus.PENDING) {
            throw new OrderException(OrderErrorCode.INVALID_PAYMENT_COMPLETE_STATUS);
        }
        this.status = OrderStatus.PAID;
        this.payment = payment;
    }

    /**
     * 배송 시작 처리 (관리자)
     */
    public void ship() {
        if (this.status != OrderStatus.PAID) {
            throw new OrderException(OrderErrorCode.INVALID_ORDER_STATUS);
        }
        this.status = OrderStatus.SHIPPED;
    }

    /**
     * 배송 완료 처리 (관리자)
     */
    public void deliver() {
        if (this.status != OrderStatus.SHIPPED) {
            throw new OrderException(OrderErrorCode.INVALID_ORDER_STATUS);
        }
        this.status = OrderStatus.DELIVERED;
    }
}
