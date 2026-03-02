package com.prj.flashdeal.domain.review.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.util.ReflectionTestUtils;

import com.prj.flashdeal.domain.member.entity.Member;
import com.prj.flashdeal.domain.member.service.MemberService;
import com.prj.flashdeal.domain.order.service.OrderService;
import com.prj.flashdeal.domain.product.entity.Product;
import com.prj.flashdeal.domain.product.entity.ProductCategory;
import com.prj.flashdeal.domain.product.service.ProductService;
import com.prj.flashdeal.domain.review.dto.request.ReviewRequest;
import com.prj.flashdeal.domain.review.dto.response.ReviewResponse;
import com.prj.flashdeal.domain.review.entity.Review;
import com.prj.flashdeal.domain.review.exception.ReviewException;
import com.prj.flashdeal.domain.review.repository.ReviewRepository;
import com.prj.flashdeal.global.response.PageResponse;

@ExtendWith(MockitoExtension.class)
@DisplayName("ReviewService 단위 테스트")
class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private MemberService memberService;

    @Mock
    private ProductService productService;

    @Mock
    private OrderService orderService;

    @InjectMocks
    private ReviewService reviewService;

    // ========== createReview ==========

    @Test
    @DisplayName("createReview 성공")
    void createReview_Success() {
        // given
        Long memberId = 1L;
        Long productId = 1L;
        Member member = createMember(memberId);
        Product product = createProduct(productId);
        ReviewRequest request = createReviewRequest(5, "좋아요");

        given(memberService.getMember(memberId)).willReturn(member);
        given(productService.getProductEntity(productId)).willReturn(product);
        given(orderService.hasPurchased(memberId, productId)).willReturn(true);
        given(reviewRepository.existsByMemberAndProduct(member, product)).willReturn(false);
        given(reviewRepository.save(any(Review.class))).willAnswer(inv -> inv.getArgument(0));

        // when
        ReviewResponse response = reviewService.createReview(memberId, productId, request);

        // then
        assertThat(response).isNotNull();
    }

    @Test
    @DisplayName("createReview 실패 - 구매하지 않은 상품")
    void createReview_Fail_NotPurchased() {
        // given
        Long memberId = 1L;
        Long productId = 1L;
        Member member = createMember(memberId);
        Product product = createProduct(productId);
        ReviewRequest request = createReviewRequest(4, "리뷰");

        given(memberService.getMember(memberId)).willReturn(member);
        given(productService.getProductEntity(productId)).willReturn(product);
        given(orderService.hasPurchased(memberId, productId)).willReturn(false);

        // when & then
        assertThatThrownBy(() -> reviewService.createReview(memberId, productId, request))
            .isInstanceOf(ReviewException.class);
    }

    @Test
    @DisplayName("createReview 실패 - 이미 리뷰를 작성한 상품")
    void createReview_Fail_AlreadyReviewed() {
        // given
        Long memberId = 1L;
        Long productId = 1L;
        Member member = createMember(memberId);
        Product product = createProduct(productId);
        ReviewRequest request = createReviewRequest(3, "또 씁니다");

        given(memberService.getMember(memberId)).willReturn(member);
        given(productService.getProductEntity(productId)).willReturn(product);
        given(orderService.hasPurchased(memberId, productId)).willReturn(true);
        given(reviewRepository.existsByMemberAndProduct(member, product)).willReturn(true);

        // when & then
        assertThatThrownBy(() -> reviewService.createReview(memberId, productId, request))
            .isInstanceOf(ReviewException.class);
    }

    // ========== getReviews ==========

    @Test
    @DisplayName("getReviews 성공 - 페이징된 리뷰 목록 반환")
    void getReviews_Success() {
        // given
        Long productId = 1L;
        Member member = createMember(1L);
        Product product = createProduct(productId);
        Review review = Review.builder()
            .member(member)
            .product(product)
            .rating(5)
            .content("좋아요")
            .build();

        Page<Review> page = new PageImpl<>(List.of(review));
        given(reviewRepository.findReviewsByProductId(productId, PageRequest.of(0, 10))).willReturn(page);

        // when
        PageResponse<ReviewResponse> response = reviewService.getReviews(productId, PageRequest.of(0, 10));

        // then
        assertThat(response).isNotNull();
        assertThat(response.getData()).hasSize(1);
    }

    // ========== 헬퍼 메서드 ==========

    private Member createMember(Long id) {
        Member member = Member.builder()
            .email("test@test.com")
            .password("pw")
            .name("테스터")
            .phoneNumber("010-1234-5678")
            .build();
        ReflectionTestUtils.setField(member, "id", id);
        return member;
    }

    private Product createProduct(Long id) {
        Product product = Product.builder()
            .name("테스트 상품")
            .description("설명")
            .price(10000)
            .stock(100)
            .category(ProductCategory.ELECTRONICS)
            .build();
        ReflectionTestUtils.setField(product, "id", id);
        return product;
    }

    private ReviewRequest createReviewRequest(int rating, String content) {
        ReviewRequest request = new ReviewRequest();
        ReflectionTestUtils.setField(request, "rating", rating);
        ReflectionTestUtils.setField(request, "content", content);
        return request;
    }
}
