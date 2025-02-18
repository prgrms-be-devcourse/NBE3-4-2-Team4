package com.NBE3_4_2_Team4.domain.product.product.repository;

import com.NBE3_4_2_Team4.domain.product.product.entity.Product;
import com.NBE3_4_2_Team4.standard.search.ProductSearchKeywordType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepositoryCustom {

    Page<Product> findByKeyword(ProductSearchKeywordType keywordType, String keyword, Pageable pageable);
}