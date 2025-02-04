package com.NBE3_4_2_Team4.domain.product.product.initData;

import com.NBE3_4_2_Team4.domain.product.category.entity.ProductCategory;
import com.NBE3_4_2_Team4.domain.product.category.repository.ProductCategoryRepository;
import com.NBE3_4_2_Team4.domain.product.product.entity.Product;
import com.NBE3_4_2_Team4.domain.product.product.repository.ProductRepository;
import com.NBE3_4_2_Team4.domain.product.saleState.entity.ProductSaleState;
import com.NBE3_4_2_Team4.domain.product.saleState.entity.SaleState;
import com.NBE3_4_2_Team4.domain.product.saleState.repository.ProductSaleStateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class ProductInitData {

    private final ProductRepository productRepository;
    private final ProductCategoryRepository productCategoryRepository;
    private final ProductSaleStateRepository productSaleStateRepository;

    @Autowired
    @Lazy
    private ProductInitData self;

    @Bean
    public ApplicationRunner productInitDataApplicationRunner() {
        return args -> {
            self.createInitProducts();
        };
    }

    @Transactional
    public void createInitProducts() {

        // TODO: 제품 생성 API 만든 후 Service에서 처리하도록 수정 필요

        if (productRepository.count() > 0) {
            return;
        }

        ProductCategory productCategory1 = ProductCategory.builder()
                .name("기프티콘")
                .build();

        ProductCategory productCategory2 = ProductCategory.builder()
                .name("음료")
                .parent(productCategory1)
                .build();

        ProductCategory productCategory3 = ProductCategory.builder()
                .name("커피")
                .parent(productCategory2)
                .build();

        List<ProductCategory> productCategories = List.of(productCategory1, productCategory2, productCategory3);
        productCategoryRepository.saveAll(productCategories);

        ProductSaleState productSaleState1 = ProductSaleState.builder()
                .name(SaleState.SOLDOUT)
                .build();

        productSaleStateRepository.save(productSaleState1);

        Product product1 = Product.builder()
                .name("스타벅스 세트1")
                .price(10000)
                .description("스타벅스 쿠폰1입니다.")
                .imageUrl("http://example.com/path/image1.jpg")
                .category(productCategory3)
                .saleState(productSaleState1)
                .build();

        Product product2 = Product.builder()
                .name("스타벅스 세트2")
                .price(15000)
                .description("스타벅스 쿠폰2입니다.")
                .imageUrl("http://example.com/path/image2.jpg")
                .category(productCategory3)
                .saleState(productSaleState1)
                .build();

        List<Product> products = List.of(product1, product2);
        productRepository.saveAll(products);
    }
}
