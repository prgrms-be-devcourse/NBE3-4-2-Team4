package com.NBE3_4_2_Team4.domain.product.product.initData;

import com.NBE3_4_2_Team4.domain.product.product.dto.ProductRequestDto;
import com.NBE3_4_2_Team4.domain.product.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.transaction.annotation.Transactional;

@Configuration
@RequiredArgsConstructor
public class ProductInitData {

    private final ProductService productService;

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

        if (productService.countProducts() > 0) {
            return;
        }

        productService.writeProduct(
                ProductRequestDto.writeItem.builder()
                        .productName("딸기라떼")
                        .productPrice(3800)
                        .productDescription("빽다방 딸기라떼입니다.")
                        .productImageUrl("http://example.com/path/pack1.jpg")
                        .productCategory("카페/빽다방")
                        .productSaleState("ONSALE")
                        .build()
        );

        productService.writeProduct(
                ProductRequestDto.writeItem.builder()
                        .productName("오리지널 핫번 + 아메리카노(L)")
                        .productPrice(6100)
                        .productDescription("이디야 오리지널 핫번 + 아메리카노(L) 세트입니다.")
                        .productImageUrl("http://example.com/path/ediya1.jpg")
                        .productCategory("카페/이디야")
                        .productSaleState("SOLDOUT")
                        .build()
        );

        productService.writeProduct(
                ProductRequestDto.writeItem.builder()
                        .productName("올리브영 기프트카드 2만원권")
                        .productPrice(20000)
                        .productDescription("올리브영 기프트카드 2만원권 입니다.")
                        .productImageUrl("http://example.com/path/gift1.jpg")
                        .productCategory("상품권/올리브영")
                        .productSaleState("ONSALE")
                        .build()
        );

        productService.writeProduct(
                ProductRequestDto.writeItem.builder()
                        .productName("교보문고 기프트카드 3만원권")
                        .productPrice(30000)
                        .productDescription("기프트카드 3만원권 입니다.")
                        .productImageUrl("http://example.com/path/gift2.jpg")
                        .productCategory("상품권/교보문고")
                        .productSaleState("ONSALE")
                        .build()
        );

        productService.writeProduct(
                ProductRequestDto.writeItem.builder()
                        .productName("뿌링클콤보+콜라1.25L")
                        .productPrice(22500)
                        .productDescription("BHC 뿌링클 콤보 + 콜라 1.25L 세트입니다.")
                        .productImageUrl("http://example.com/path/chicken.jpg")
                        .productCategory("치킨/BHC")
                        .productSaleState("ONSALE")
                        .build()
        );
    }
}