package com.NBE3_4_2_Team4.domain.product.category.repository;

import com.NBE3_4_2_Team4.domain.product.category.entity.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductCategoryRepository extends JpaRepository<ProductCategory, Long> {

    List<ProductCategory> findByNameContainingOrderByIdAsc(String categoryKeyword);
}
