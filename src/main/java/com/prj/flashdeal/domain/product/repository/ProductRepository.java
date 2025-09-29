package com.prj.flashdeal.domain.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.prj.flashdeal.domain.product.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long>, ProductRepositoryCustom {
}
