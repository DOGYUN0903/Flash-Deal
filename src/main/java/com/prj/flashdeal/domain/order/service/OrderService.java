package com.prj.flashdeal.domain.order.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prj.flashdeal.domain.cart.entity.CartItem;
import com.prj.flashdeal.domain.cart.service.CartService;
import com.prj.flashdeal.domain.member.entity.Member;
import com.prj.flashdeal.domain.member.service.MemberService;
import com.prj.flashdeal.domain.order.dto.request.DirectOrderRequest;
import com.prj.flashdeal.domain.order.dto.request.OrderCreateRequest;
import com.prj.flashdeal.domain.order.dto.response.OrderResponse;
import com.prj.flashdeal.domain.order.dto.response.OrderSummaryResponse;
import com.prj.flashdeal.domain.order.entity.DeliveryAddress;
import com.prj.flashdeal.domain.order.entity.Order;
import com.prj.flashdeal.domain.order.entity.OrderItem;
import com.prj.flashdeal.domain.order.exception.OrderErrorCode;
import com.prj.flashdeal.domain.order.exception.OrderException;
import com.prj.flashdeal.domain.order.repository.OrderRepository;
import com.prj.flashdeal.domain.product.entity.Product;
import com.prj.flashdeal.domain.product.service.ProductService;
import com.prj.flashdeal.global.response.PageResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final MemberService memberService;
    private final CartService cartService;
    private final ProductService productService;

    /**
     * 주문 생성 (장바구니 → 주문 전환)
     */
    @Transactional
    public OrderResponse createOrderFromCart(Long memberId, OrderCreateRequest request) {
        Member member = memberService.getMember(memberId);

        // 장바구니 조회
        List<CartItem> cartItems = cartService.getCartItemsForOrder(member);
        if (cartItems.isEmpty()) {
            throw new OrderException(OrderErrorCode.EMPTY_CART);
        }

        // 배송지 정보 생성
        DeliveryAddress deliveryAddress = DeliveryAddress.of(
            request.getRecipientName(),
            request.getPhoneNumber(),
            request.getZipcode(),
            request.getStreet(),
            request.getDetail()
        );

        // 주문 생성
        Order order = Order.createOrder(member, deliveryAddress);

        // 장바구니 항목 → 주문 항목 변환 및 재고 감소
        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();

            // 재고 감소 (ProductService 사용)
            productService.decreaseStock(product.getId(), cartItem.getQuantity());

            // 주문 항목 생성
            OrderItem orderItem = OrderItem.createOrderItem(product, cartItem.getQuantity());
            order.addOrderItem(orderItem);
        }

        // 주문 저장
        Order savedOrder = orderRepository.save(order);

        // 장바구니 비우기
        cartService.clearCartForOrder(member);

        return OrderResponse.from(savedOrder);
    }

    /**
     * 바로 구매 (장바구니 거치지 않고 즉시 주문)
     */
    @Transactional
    public OrderResponse createDirectOrder(Long memberId, DirectOrderRequest request) {
        Member member = memberService.getMember(memberId);

        // 상품 조회 및 검증
        Product product = productService.findCartableProduct(request.getProductId());

        // 배송지 정보 생성
        DeliveryAddress deliveryAddress = DeliveryAddress.of(
            request.getRecipientName(),
            request.getPhoneNumber(),
            request.getZipcode(),
            request.getStreet(),
            request.getDetail()
        );

        // 주문 생성
        Order order = Order.createOrder(member, deliveryAddress);

        // 재고 감소
        productService.decreaseStock(product.getId(), request.getQuantity());

        // 주문 항목 생성
        OrderItem orderItem = OrderItem.createOrderItem(product, request.getQuantity());
        order.addOrderItem(orderItem);

        // 주문 저장
        Order savedOrder = orderRepository.save(order);

        return OrderResponse.from(savedOrder);
    }

    /**
     * 주문 단건 조회
     */
    @Transactional(readOnly = true)
    public OrderResponse getOrder(Long memberId, Long orderId) {
        Order order = orderRepository.findByIdAndMemberId(orderId, memberId)
            .orElseThrow(() -> new OrderException(OrderErrorCode.ORDER_NOT_FOUND));

        return OrderResponse.from(order);
    }

    /**
     * 주문 목록 조회 (DTO Projection, 페이징)
     */
    @Transactional(readOnly = true)
    public PageResponse<OrderSummaryResponse> getOrders(Long memberId, Pageable pageable) {
        Member member = memberService.getMember(memberId);

        Page<OrderSummaryResponse> responsePage = orderRepository.findOrderSummariesByMember(member, pageable);
        return new PageResponse<>(responsePage);
    }

    /**
     * 주문 취소
     */
    @Transactional
    public void cancelOrder(Long memberId, Long orderId) {
        Order order = orderRepository.findByIdAndMemberId(orderId, memberId)
            .orElseThrow(() -> new OrderException(OrderErrorCode.ORDER_NOT_FOUND));

        // 주문 취소 (OrderException 발생 가능)
        order.cancel();

        // 재고 복구
        for (OrderItem orderItem : order.getOrderItems()) {
            productService.increaseStock(orderItem.getProductId(), orderItem.getQuantity());
        }
    }

    /**
     * 주문 조회 (내부 사용)
     */
    @Transactional(readOnly = true)
    public Order findOrder(Long orderId) {
        return orderRepository.findById(orderId)
            .orElseThrow(() -> new OrderException(OrderErrorCode.ORDER_NOT_FOUND));
    }

    // -------------------- 관리자 전용 메서드 --------------------

    /**
     * 전체 주문 목록 조회 (관리자) - 페이징 (DTO Projection)
     */
    @Transactional(readOnly = true)
    public PageResponse<OrderSummaryResponse> getAllOrders(Pageable pageable) {
        Page<OrderSummaryResponse> responsePage = orderRepository.findAllOrderSummaries(pageable);
        return new PageResponse<>(responsePage);
    }

    /**
     * 주문 상세 조회 (관리자)
     */
    @Transactional(readOnly = true)
    public OrderResponse getOrderForAdmin(Long orderId) {
        Order order = findOrder(orderId);
        return OrderResponse.from(order);
    }

    /**
     * 배송 시작 처리 (관리자)
     */
    @Transactional
    public void startShipping(Long orderId) {
        Order order = findOrder(orderId);
        order.ship();
    }

    /**
     * 배송 완료 처리 (관리자)
     */
    @Transactional
    public void completeDelivery(Long orderId) {
        Order order = findOrder(orderId);
        order.deliver();
    }
}
