package com.NBE3_4_2_Team4.domain.product.product.repository;

import com.NBE3_4_2_Team4.domain.product.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByCategoryIdIn(Set<Long> categoryIds);

    Page<Product> findByCategoryIdIn(Set<Long> categoryIds, PageRequest pageRequest);
}
