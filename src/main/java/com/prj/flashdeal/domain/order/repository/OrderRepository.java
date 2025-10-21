package com.prj.flashdeal.domain.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.prj.flashdeal.domain.order.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Long>, OrderRepositoryCustom {
}