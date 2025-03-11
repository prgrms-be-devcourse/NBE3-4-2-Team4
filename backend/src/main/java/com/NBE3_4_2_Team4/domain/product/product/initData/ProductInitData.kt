package com.NBE3_4_2_Team4.domain.product.product.initData

import com.NBE3_4_2_Team4.domain.product.product.dto.ProductRequestDto.WriteItem
import com.NBE3_4_2_Team4.domain.product.product.service.ProductService
import jakarta.transaction.Transactional
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy

@Configuration
class ProductInitData(

    private val productService: ProductService
) {

    companion object {
        val PRODUCT_LIST = listOf(
            WriteItem(
                productName = "딸기라떼",
                productPrice = 3800,
                productDescription = "빽다방 딸기라떼입니다.",
                productImageUrl = "https://img1.kakaocdn.net/thumb/C320x320@2x.fwebp.q82/?fname=https%3A%2F%2Fst.kakaocdn.net%2Fproduct%2Fgift%2Fproduct%2F20240226145052_4fdaf86956ad4dc59d32a960b6e0704d.jpg",
                productCategory = "카페/빽다방",
                productSaleState = "AVAILABLE"
            ),
            WriteItem(
                productName = "오리지널 핫번 + 아메리카노(L)",
                productPrice = 6100,
                productDescription = "이디야 오리지널 핫번 + 아메리카노(L) 세트입니다.",
                productImageUrl = "https://img1.kakaocdn.net/thumb/C320x320@2x.fwebp.q82/?fname=https%3A%2F%2Fst.kakaocdn.net%2Fproduct%2Fgift%2Fproduct%2F20221220171143_9a6b9134b51f457392ec1d249986161b.jpg",
                productCategory = "카페/이디야",
                productSaleState = "UNAVAILABLE"
            ),
            WriteItem(
                productName = "올리브영 기프트카드 2만원권",
                productPrice = 20000,
                productDescription = "올리브영 기프트카드 2만원권 입니다.",
                productImageUrl = "https://img1.kakaocdn.net/thumb/C320x320@2x.fwebp.q82/?fname=https%3A%2F%2Fst.kakaocdn.net%2Fproduct%2Fgift%2Fproduct%2F20241231095139_5ca608e7c5a94c88a3c8903114ec7f23.jpg",
                productCategory = "상품권/올리브영",
                productSaleState = "AVAILABLE"
            ),
            WriteItem(
                productName = "교보문고 기프트카드 3만원권",
                productPrice = 30000,
                productDescription = "기프트카드 3만원권 입니다.",
                productImageUrl = "https://img1.kakaocdn.net/thumb/C320x320@2x.fwebp.q82/?fname=https%3A%2F%2Fst.kakaocdn.net%2Fproduct%2Fgift%2Fproduct%2F20250114094012_aeb9ebbb0fea4defa8c9316758ab1b2a.jpg",
                productCategory = "상품권/교보문고",
                productSaleState = "AVAILABLE"
            ),
            WriteItem(
                productName = "뿌링클콤보+콜라1.25L",
                productPrice = 22500,
                productDescription = "BHC 뿌링클 콤보 + 콜라 1.25L 세트입니다.",
                productImageUrl = "https://img1.kakaocdn.net/thumb/C320x320@2x.fwebp.q82/?fname=https%3A%2F%2Fst.kakaocdn.net%2Fproduct%2Fgift%2Fproduct%2F20230419095658_3ba18e7a2b514014ba7ec4d4c060f98a.jpg",
                productCategory = "치킨/BHC",
                productSaleState = "AVAILABLE"
            ),
            WriteItem(
                productName = "초코라떼",
                productPrice = 3500,
                productDescription = "빽다방 초코라떼(HOT) 입니다.",
                productImageUrl = "https://img1.kakaocdn.net/thumb/C320x320@2x.fwebp.q82/?fname=https%3A%2F%2Fst.kakaocdn.net%2Fproduct%2Fgift%2Fproduct%2F20240226143254_e64257e9f3714329a9e0bcf39a9985bb.jpg",
                productCategory = "카페/빽다방",
                productSaleState = "AVAILABLE"
            ),
            WriteItem(
                productName = "블랙 밀크티+펄 L",
                productPrice = 5000,
                productDescription = "공차 블랙 밀크티 + 펄 L 입니다.",
                productImageUrl = "https://img1.kakaocdn.net/thumb/C320x320@2x.fwebp.q82/?fname=https%3A%2F%2Fst.kakaocdn.net%2Fproduct%2Fgift%2Fproduct%2F20231226175703_c12e4d8a3b4b43e99575826bea89b635.jpg",
                productCategory = "카페/공차",
                productSaleState = "AVAILABLE"
            ),
            WriteItem(
                productName = "(ICE)바닐라라떼",
                productPrice = 3400,
                productDescription = "메가MGC커피 (ICE)바닐라라떼 입니다.",
                productImageUrl = "https://img1.kakaocdn.net/thumb/C320x320@2x.fwebp.q82/?fname=https%3A%2F%2Fst.kakaocdn.net%2Fproduct%2Fgift%2Fproduct%2F20220622112659_d38714c17a064a0d927201a514f06594.jpg",
                productCategory = "카페/메가MGC커피",
                productSaleState = "AVAILABLE"
            ),
            WriteItem(
                productName = "핸드 크림 30ML (+ 로즈엽서 증정)",
                productPrice = 33000,
                productDescription = "조말론런던의 핸드 크림 30ML (+ 로즈엽서 증정) 입니다.",
                productImageUrl = "https://img1.kakaocdn.net/thumb/C320x320@2x.fwebp.q82/?fname=https%3A%2F%2Fst.kakaocdn.net%2Fproduct%2Fgift%2Fproduct%2F20250131190012_fa158fa70a4342ab98696b361883cd3a.jpg",
                productCategory = "화장품/조말론런던",
                productSaleState = "AVAILABLE"
            ),
            WriteItem(
                productName = "핸드 크림 (4종 택1)",
                productPrice = 66000,
                productDescription = "딥티크 핸드크림 (4종 택1) 입니다.",
                productImageUrl = "https://img1.kakaocdn.net/thumb/C320x320@2x.fwebp.q82/?fname=https%3A%2F%2Fst.kakaocdn.net%2Fproduct%2Fgift%2Fproduct%2F20220105150412_86151986eb114a35a0609a6c63346651.jpg",
                productCategory = "화장품/딥티크",
                productSaleState = "AVAILABLE"
            ),
            WriteItem(
                productName = "시그니처 생딸기 우유생크림케이크",
                productPrice = 33000,
                productDescription = "파리바게뜨 시그니처 생딸기 우유생크림케이크 입니다.",
                productImageUrl = "https://img1.kakaocdn.net/thumb/C320x320@2x.fwebp.q82/?fname=https%3A%2F%2Fst.kakaocdn.net%2Fproduct%2Fgift%2Fproduct%2F20230201162647_9d2141090c714b4d9fee0ca6e578b205.jpg",
                productCategory = "케잌/파리바게뜨",
                productSaleState = "AVAILABLE"
            ),
            WriteItem(
                productName = "스트로베리 초콜릿 생크림",
                productPrice = 33000,
                productDescription = "투썸플레이스 스트로베리 초콜릿 생크림 케이크 입니다.",
                productImageUrl = "https://img1.kakaocdn.net/thumb/C320x320@2x.fwebp.q82/?fname=https%3A%2F%2Fst.kakaocdn.net%2Fproduct%2Fgift%2Fproduct%2F20250116093155_3b51da3d48264358b2b1e58e699a2eba.jpg",
                productCategory = "케잌/파리바게뜨",
                productSaleState = "AVAILABLE"
            ),
            WriteItem(
                productName = "페레로로쉐 T8 하트",
                productPrice = 9900,
                productDescription = "페레로로쉐 초콜릿 8개입 상품 입니다.",
                productImageUrl = "https://img1.kakaocdn.net/thumb/C320x320@2x.fwebp.q82/?fname=https%3A%2F%2Fst.kakaocdn.net%2Fproduct%2Fgift%2Fproduct%2F20241108170007_2599f409fcb1493a80ec7f4aaf0d8656.jpg",
                productCategory = "디저트/페레로로쉐",
                productSaleState = "AVAILABLE"
            ),
            WriteItem(
                productName = "허니콤보",
                productPrice = 23000,
                productDescription = "교촌 허니콤보 입니다.",
                productImageUrl = "https://img1.kakaocdn.net/thumb/C320x320@2x.fwebp.q82/?fname=https%3A%2F%2Fst.kakaocdn.net%2Fproduct%2Fgift%2Fproduct%2F20230420141214_e6319192399544e5a7f5f1948be9e028.jpg",
                productCategory = "치킨/교촌치킨",
                productSaleState = "AVAILABLE"
            ),
            WriteItem(
                productName = "양념치킨+콜라1.25L",
                productPrice = 24000,
                productDescription = "BBQ 양념치킨 + 콜라 1.25L 입니다.",
                productImageUrl = "https://img1.kakaocdn.net/thumb/C320x320@2x.fwebp.q82/?fname=https%3A%2F%2Fst.kakaocdn.net%2Fproduct%2Fgift%2Fproduct%2F20241030171627_5c43e4fc783d400c9aee87640d1c9ced.jpg",
                productCategory = "치킨/BBQ",
                productSaleState = "UPCOMING"
            )
        )
    }

    @Autowired
    @Lazy
    private lateinit var self: ProductInitData

    @Bean
    fun productInitDataApplicationRunner() = ApplicationRunner {
        self.createInitProducts()
    }

    @Transactional
    fun createInitProducts() {
        if (productService.countProducts() > 0) return

        PRODUCT_LIST.forEach { writeItem ->
            productService.writeProduct(writeItem)
        }
    }
}