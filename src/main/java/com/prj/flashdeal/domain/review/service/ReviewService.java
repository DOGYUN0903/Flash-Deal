package com.prj.flashdeal.domain.review.service;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prj.flashdeal.domain.member.entity.Member;
import com.prj.flashdeal.domain.member.service.MemberService;
import com.prj.flashdeal.domain.order.service.OrderService;
import com.prj.flashdeal.domain.product.entity.Product;
import com.prj.flashdeal.domain.product.service.ProductService;
import com.prj.flashdeal.domain.review.dto.request.ReviewRequest;
import com.prj.flashdeal.domain.review.dto.response.ReviewResponse;
import com.prj.flashdeal.domain.review.entity.Review;
import com.prj.flashdeal.domain.review.exception.ReviewErrorCode;
import com.prj.flashdeal.domain.review.exception.ReviewException;
import com.prj.flashdeal.domain.review.repository.ReviewRepository;
import com.prj.flashdeal.global.response.PageResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final MemberService memberService;
    private final ProductService productService;
    private final OrderService orderService;

    @Transactional
    public ReviewResponse createReview(Long memberId, Long productId, ReviewRequest request) {
        Member member = memberService.getMember(memberId);
        Product product = productService.getProductEntity(productId);

        if (!orderService.hasPurchased(memberId, productId)) {
            throw new ReviewException(ReviewErrorCode.NOT_PURCHASED);
        }

        if (reviewRepository.existsByMemberAndProduct(member, product)) {
            throw new ReviewException(ReviewErrorCode.ALREADY_REVIEWED);
        }

        Review review = Review.builder()
            .member(member)
            .product(product)
            .rating(request.getRating())
            .content(request.getContent())
            .build();

        return ReviewResponse.from(reviewRepository.save(review));
    }

    @Transactional(readOnly = true)
    public PageResponse<ReviewResponse> getReviews(Long productId, Pageable pageable) {
        return new PageResponse<>(
            reviewRepository.findReviewsByProductId(productId, pageable)
                .map(ReviewResponse::from)
        );
    }
}
