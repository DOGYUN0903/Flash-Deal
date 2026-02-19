package com.prj.flashdeal.domain.deal.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.prj.flashdeal.domain.deal.entity.Deal;

public interface DealRepository extends JpaRepository<Deal, Long> {
    Page<Deal> findAllByDeletedAtIsNull(Pageable pageable);
}
