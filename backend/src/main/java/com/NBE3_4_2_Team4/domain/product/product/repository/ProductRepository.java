package com.NBE3_4_2_Team4.domain.product.product.repository;

import com.NBE3_4_2_Team4.domain.product.product.entity.Product;
import com.NBE3_4_2_Team4.domain.product.saleState.entity.SaleState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByCategoryIdIn(Set<Long> categoryIds);

    Page<Product> findByCategoryIdIn(Set<Long> categoryIds, Pageable pageable);

    @Query("SELECT p " +
            "FROM Product p " +
                "JOIN ProductSaleState pss ON p.saleState = pss " +
            "WHERE pss.name = :name")
    Page<Product> findBySaleStateLike(@Param("name") SaleState saleState, Pageable pageable);
}
