package com.prj.flashdeal.domain.deal.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.prj.flashdeal.domain.deal.dto.request.DealOrderRequest;
import com.prj.flashdeal.domain.deal.dto.response.DealResponse;
import com.prj.flashdeal.domain.deal.entity.Deal;
import com.prj.flashdeal.domain.deal.exception.DealException;
import com.prj.flashdeal.domain.deal.repository.DealRepository;
import com.prj.flashdeal.domain.member.entity.Member;
import com.prj.flashdeal.domain.member.service.MemberService;
import com.prj.flashdeal.domain.order.dto.response.OrderResponse;
import com.prj.flashdeal.domain.order.entity.Order;
import com.prj.flashdeal.domain.order.repository.OrderRepository;
import com.prj.flashdeal.domain.payment.client.TossPaymentClient;
import com.prj.flashdeal.domain.payment.dto.response.TossPaymentResponse;
import com.prj.flashdeal.domain.product.entity.Product;
import com.prj.flashdeal.domain.product.entity.ProductCategory;
import com.prj.flashdeal.domain.product.repository.ProductRepository;
import com.prj.flashdeal.domain.stock.service.StockService;

@ExtendWith(MockitoExtension.class)
@DisplayName("DealService 단위 테스트")
class DealServiceTest {

    @Mock private DealRepository dealRepository;
    @Mock private OrderRepository orderRepository;
    @Mock private ProductRepository productRepository;
    @Mock private MemberService memberService;
    @Mock private StockService stockService;
    @Mock private TossPaymentClient tossPaymentClient;

    @InjectMocks
    private DealService dealService;

    @Test
    @DisplayName("딜 주문 성공")
    void createDealOrder_Success() {
        // given
        Long memberId = 1L;
        Long dealId = 1L;
        int discountPrice = 89000;

        Member member = createMember(memberId);
        Product product = createProduct(1L, 129000);
        Deal deal = createActiveDeal(dealId, product, discountPrice);
        DealOrderRequest request = createDealOrderRequest("tgen_test_key", "DEAL-1-uuid", discountPrice, 1);

        given(memberService.getMember(memberId)).willReturn(member);
        given(dealRepository.findById(dealId)).willReturn(Optional.of(deal));
        willDoNothing().given(stockService).decreaseStock(anyLong(), anyInt());
        given(tossPaymentClient.confirm(anyString(), anyString(), anyInt())).willReturn(mock(TossPaymentResponse.class));
        given(orderRepository.save(any(Order.class))).willAnswer(inv -> {
            Order saved = inv.getArgument(0);
            ReflectionTestUtils.setField(saved, "id", 1L);
            return saved;
        });

        // when
        OrderResponse response = dealService.createDealOrder(memberId, dealId, request);

        // then
        assertThat(response).isNotNull();
        verify(stockService).decreaseStock(product.getId(), 1);
        verify(tossPaymentClient).confirm("tgen_test_key", "DEAL-1-uuid", discountPrice);
    }

    @Test
    @DisplayName("딜 주문 실패 - ACTIVE 상태 아닌 딜")
    void createDealOrder_Fail_DealNotActive() {
        // given
        Long memberId = 1L;
        Long dealId = 1L;

        Member member = createMember(memberId);
        Product product = createProduct(1L, 129000);
        Deal deal = createScheduledDeal(dealId, product, 89000);
        DealOrderRequest request = createDealOrderRequest("tgen_test_key", "DEAL-1-uuid", 89000, 1);

        given(memberService.getMember(memberId)).willReturn(member);
        given(dealRepository.findById(dealId)).willReturn(Optional.of(deal));

        // when & then
        assertThatThrownBy(() -> dealService.createDealOrder(memberId, dealId, request))
            .isInstanceOf(DealException.class);
    }

    @Test
    @DisplayName("딜 주문 실패 - 결제 금액 불일치")
    void createDealOrder_Fail_AmountMismatch() {
        // given
        Long memberId = 1L;
        Long dealId = 1L;

        Member member = createMember(memberId);
        Product product = createProduct(1L, 129000);
        Deal deal = createActiveDeal(dealId, product, 89000);
        DealOrderRequest request = createDealOrderRequest("tgen_test_key", "DEAL-1-uuid", 50000, 1);

        given(memberService.getMember(memberId)).willReturn(member);
        given(dealRepository.findById(dealId)).willReturn(Optional.of(deal));

        // when & then
        assertThatThrownBy(() -> dealService.createDealOrder(memberId, dealId, request))
            .isInstanceOf(DealException.class);
    }

    @Test
    @DisplayName("딜 조회 성공")
    void getDeal_Success() {
        // given
        Long dealId = 1L;
        Product product = createProduct(1L, 129000);
        Deal deal = createActiveDeal(dealId, product, 89000);

        given(dealRepository.findById(dealId)).willReturn(Optional.of(deal));
        given(stockService.getStock(product.getId())).willReturn(50);

        // when
        DealResponse response = dealService.getDeal(dealId);

        // then
        assertThat(response).isNotNull();
        assertThat(response.dealId()).isEqualTo(dealId);
        assertThat(response.remainingStock()).isEqualTo(50);
        assertThat(response.discountPrice()).isEqualTo(89000);
    }

    @Test
    @DisplayName("딜 조회 실패 - 존재하지 않는 딜")
    void getDeal_Fail_NotFound() {
        // given
        given(dealRepository.findById(anyLong())).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> dealService.getDeal(999L))
            .isInstanceOf(DealException.class);
    }

    // ========== 헬퍼 메서드 ==========

    private Member createMember(Long id) {
        Member member = Member.builder()
            .email("test@test.com")
            .password("password123")
            .name("테스터")
            .phoneNumber("010-1234-5678")
            .build();
        ReflectionTestUtils.setField(member, "id", id);
        return member;
    }

    private Product createProduct(Long id, int price) {
        Product product = Product.builder()
            .name("테스트 상품")
            .description("테스트 상품 설명")
            .price(price)
            .category(ProductCategory.ELECTRONICS)
            .build();
        ReflectionTestUtils.setField(product, "id", id);
        product.markOnSale();
        return product;
    }

    private Deal createActiveDeal(Long id, Product product, int discountPrice) {
        Deal deal = Deal.builder()
            .product(product)
            .title("테스트 딜")
            .discountPrice(discountPrice)
            .startAt(LocalDateTime.now().minusHours(1))
            .endAt(LocalDateTime.now().plusHours(1))
            .build();
        deal.activate();
        ReflectionTestUtils.setField(deal, "id", id);
        return deal;
    }

    private Deal createScheduledDeal(Long id, Product product, int discountPrice) {
        Deal deal = Deal.builder()
            .product(product)
            .title("예정 딜")
            .discountPrice(discountPrice)
            .startAt(LocalDateTime.now().plusHours(1))
            .endAt(LocalDateTime.now().plusHours(3))
            .build();
        ReflectionTestUtils.setField(deal, "id", id);
        return deal;
    }

    private DealOrderRequest createDealOrderRequest(String paymentKey, String orderId, int amount, int quantity) {
        DealOrderRequest request = new DealOrderRequest();
        ReflectionTestUtils.setField(request, "paymentKey", paymentKey);
        ReflectionTestUtils.setField(request, "orderId", orderId);
        ReflectionTestUtils.setField(request, "amount", amount);
        ReflectionTestUtils.setField(request, "quantity", quantity);
        return request;
    }
}
