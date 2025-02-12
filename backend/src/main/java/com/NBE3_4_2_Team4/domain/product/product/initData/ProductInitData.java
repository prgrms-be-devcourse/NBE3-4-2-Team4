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
                        .productImageUrl("https://img1.kakaocdn.net/thumb/C320x320@2x.fwebp.q82/?fname=https%3A%2F%2Fst.kakaocdn.net%2Fproduct%2Fgift%2Fproduct%2F20240226145052_4fdaf86956ad4dc59d32a960b6e0704d.jpg")
                        .productCategory("카페/빽다방")
                        .productSaleState("ONSALE")
                        .build()
        );

        productService.writeProduct(
                ProductRequestDto.writeItem.builder()
                        .productName("오리지널 핫번 + 아메리카노(L)")
                        .productPrice(6100)
                        .productDescription("이디야 오리지널 핫번 + 아메리카노(L) 세트입니다.")
                        .productImageUrl("https://img1.kakaocdn.net/thumb/C320x320@2x.fwebp.q82/?fname=https%3A%2F%2Fst.kakaocdn.net%2Fproduct%2Fgift%2Fproduct%2F20221220171143_9a6b9134b51f457392ec1d249986161b.jpg")
                        .productCategory("카페/이디야")
                        .productSaleState("SOLDOUT")
                        .build()
        );

        productService.writeProduct(
                ProductRequestDto.writeItem.builder()
                        .productName("올리브영 기프트카드 2만원권")
                        .productPrice(20000)
                        .productDescription("올리브영 기프트카드 2만원권 입니다.")
                        .productImageUrl("https://img1.kakaocdn.net/thumb/C320x320@2x.fwebp.q82/?fname=https%3A%2F%2Fst.kakaocdn.net%2Fproduct%2Fgift%2Fproduct%2F20241231095139_5ca608e7c5a94c88a3c8903114ec7f23.jpg")
                        .productCategory("상품권/올리브영")
                        .productSaleState("ONSALE")
                        .build()
        );

        productService.writeProduct(
                ProductRequestDto.writeItem.builder()
                        .productName("교보문고 기프트카드 3만원권")
                        .productPrice(30000)
                        .productDescription("기프트카드 3만원권 입니다.")
                        .productImageUrl("https://img1.kakaocdn.net/thumb/C320x320@2x.fwebp.q82/?fname=https%3A%2F%2Fst.kakaocdn.net%2Fproduct%2Fgift%2Fproduct%2F20250114094012_aeb9ebbb0fea4defa8c9316758ab1b2a.jpg")
                        .productCategory("상품권/교보문고")
                        .productSaleState("ONSALE")
                        .build()
        );

        productService.writeProduct(
                ProductRequestDto.writeItem.builder()
                        .productName("뿌링클콤보+콜라1.25L")
                        .productPrice(22500)
                        .productDescription("BHC 뿌링클 콤보 + 콜라 1.25L 세트입니다.")
                        .productImageUrl("https://img1.kakaocdn.net/thumb/C320x320@2x.fwebp.q82/?fname=https%3A%2F%2Fst.kakaocdn.net%2Fproduct%2Fgift%2Fproduct%2F20230419095658_3ba18e7a2b514014ba7ec4d4c060f98a.jpg")
                        .productCategory("치킨/BHC")
                        .productSaleState("ONSALE")
                        .build()
        );

        for(int i = 0; i < 10; i++) {
            productService.writeProduct(
                    ProductRequestDto.writeItem.builder()
                            .productName("상품명 " + i)
                            .productPrice(i * 100)
                            .productDescription("상품 설명 " + i)
                            .productImageUrl("https://search.pstatic.net/common/?src=http%3A%2F%2Fblogfiles.naver.net%2FMjAyNDAyMDZfODUg%2FMDAxNzA3MTQ2MjQyNTYw.dzcv_P_Q9declRlLANq5VfofXO-u5kOfsBrvJiAujJMg.SgqAhgPYegriKDz6-rQe7eDR9-quiNAxiUVyYdWDE2wg.JPEG.hyuni_525%2FIMG_3800.JPG")
                            .productCategory("카페/스타벅스")
                            .productSaleState("ONSALE")
                            .build()
            );
        }
    }
}