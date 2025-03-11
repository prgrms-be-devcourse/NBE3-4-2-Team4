package com.NBE3_4_2_Team4.domain.product.order.repository;

import com.NBE3_4_2_Team4.domain.product.order.entity.ProductOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductOrderRepository extends JpaRepository<ProductOrder, Integer> {
}
