package com.prj.flashdeal.domain.product.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.prj.flashdeal.domain.product.dto.request.ProductSearchCondForAdmin;
import com.prj.flashdeal.domain.product.dto.request.ProductSearchCondForUser;
import com.prj.flashdeal.domain.product.dto.response.ProductSummaryResponse;

public interface ProductRepositoryCustom {

    Page<ProductSummaryResponse> searchProductsForAdmin(ProductSearchCondForAdmin cond, Pageable pageable);

    Page<ProductSummaryResponse> searchProductsForUser(ProductSearchCondForUser cond, Pageable pageable);
}
