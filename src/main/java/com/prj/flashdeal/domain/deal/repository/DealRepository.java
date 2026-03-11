package com.prj.flashdeal.domain.deal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.prj.flashdeal.domain.deal.entity.Deal;
import com.prj.flashdeal.domain.deal.entity.DealStatus;

public interface DealRepository extends JpaRepository<Deal, Long>, DealRepositoryCustom {
    List<Deal> findByStatus(DealStatus status);
}
