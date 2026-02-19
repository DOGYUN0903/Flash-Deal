package com.prj.flashdeal.domain.deal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.prj.flashdeal.domain.deal.entity.Deal;

public interface DealRepository extends JpaRepository<Deal, Long> {
    List<Deal> findAllByDeletedAtIsNullOrderByOpenTimeDesc();
}
