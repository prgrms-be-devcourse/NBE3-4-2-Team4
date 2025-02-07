package com.NBE3_4_2_Team4.domain.product.category.repository;

import com.NBE3_4_2_Team4.domain.product.category.entity.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductCategoryRepository extends JpaRepository<ProductCategory, Long> {

    List<ProductCategory> findByNameContainingOrderByIdAsc(String categoryKeyword);

    Optional<ProductCategory> findByNameAndParent(String categoryName, ProductCategory parent);
}
