package com.NBE3_4_2_Team4.domain.product.saleState.repository;

import com.NBE3_4_2_Team4.domain.product.saleState.entity.ProductSaleState;
import com.NBE3_4_2_Team4.domain.product.saleState.entity.SaleState;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductSaleStateRepository extends JpaRepository<ProductSaleState, Long> {

    Optional<ProductSaleState> findByName(SaleState name);
}
