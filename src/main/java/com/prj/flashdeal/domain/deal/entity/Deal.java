package com.prj.flashdeal.domain.deal.entity;

import java.time.LocalDateTime;

import com.prj.flashdeal.domain.deal.exception.DealErrorCode;
import com.prj.flashdeal.domain.deal.exception.DealException;
import com.prj.flashdeal.domain.product.entity.Product;
import com.prj.flashdeal.global.entity.BaseTimeEntity;

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
import jakarta.persistence.Table;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "deals")
public class Deal extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "deal_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private Integer discountPrice;

    @Column(nullable = false)
    private LocalDateTime startAt;

    @Column(nullable = false)
    private LocalDateTime endAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DealStatus status;

    @Builder
    private Deal(Product product, String title, Integer discountPrice,
                 LocalDateTime startAt, LocalDateTime endAt) {
        this.product = product;
        this.title = title;
        this.discountPrice = discountPrice;
        this.startAt = startAt;
        this.endAt = endAt;
        this.status = DealStatus.SCHEDULED;
    }

    public void validateActive() {
        if (this.status != DealStatus.ACTIVE) {
            throw new DealException(DealErrorCode.DEAL_NOT_ACTIVE);
        }
    }

    public void activate() {
        this.status = DealStatus.ACTIVE;
    }

    public void end() {
        this.status = DealStatus.ENDED;
    }
}
