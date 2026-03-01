package com.prj.flashdeal.domain.review.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.prj.flashdeal.domain.member.entity.Member;
import com.prj.flashdeal.domain.product.entity.Product;
import com.prj.flashdeal.domain.review.entity.Review;

public interface ReviewRepository extends JpaRepository<Review, Long>, ReviewRepositoryCustom {

    boolean existsByMemberAndProduct(Member member, Product product);
}
