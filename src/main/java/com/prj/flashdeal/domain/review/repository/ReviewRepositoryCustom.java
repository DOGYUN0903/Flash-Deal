package com.prj.flashdeal.domain.review.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.prj.flashdeal.domain.review.entity.Review;

public interface ReviewRepositoryCustom {

    Page<Review> findReviewsByProductId(Long productId, Pageable pageable);
}
