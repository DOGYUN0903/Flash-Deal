package com.prj.flashdeal.domain.deal.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.prj.flashdeal.domain.deal.entity.Deal;

public interface DealRepository extends JpaRepository<Deal, Long> {
}
