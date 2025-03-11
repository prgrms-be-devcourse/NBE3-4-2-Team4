package com.NBE3_4_2_Team4.domain.product.product.repository

import com.NBE3_4_2_Team4.domain.product.product.entity.Product
import com.NBE3_4_2_Team4.domain.product.product.entity.QProduct.product
import com.NBE3_4_2_Team4.domain.product.saleState.entity.SaleState
import com.NBE3_4_2_Team4.standard.search.ProductSearchKeywordType
import com.querydsl.core.BooleanBuilder
import com.querydsl.jpa.impl.JPAQuery
import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.persistence.EntityManager
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.support.PageableExecutionUtils
import org.springframework.stereotype.Repository

@Repository
class ProductRepositoryImpl(
    em: EntityManager
) : ProductRepositoryCustom {

    private val queryFactory = JPAQueryFactory(em)

    override fun findByKeyword(
        searchKeywordType: ProductSearchKeywordType,
        searchKeyword: String,
        categoryKeyword: String,
        saleState: SaleState,
        pageable: Pageable
    ): Page<Product> {

        val builder = BooleanBuilder().apply {

            // 검색 유형에 따라 조건 추가
            when (searchKeywordType) {
                ProductSearchKeywordType.NAME -> and(product.name.containsIgnoreCase(searchKeyword))

                ProductSearchKeywordType.CATEGORY -> and(
                    product.category.name.containsIgnoreCase(searchKeyword)
                        .or(product.category.parent.name.containsIgnoreCase(searchKeyword))
                )

                else -> and(
                    product.name.containsIgnoreCase(searchKeyword)
                        .or(product.category.name.containsIgnoreCase(searchKeyword))
                        .or(product.category.parent.name.containsIgnoreCase(searchKeyword))
                )
            }

            // 카테고리에 따라 조건 추가
            if (categoryKeyword.isNotBlank()) {
                and(
                    product.category.name.containsIgnoreCase(categoryKeyword)
                        .or(product.category.parent.name.containsIgnoreCase(categoryKeyword))
                )
            }

            // 판매 상태에 따라 조건 추가
            if (saleState != SaleState.ALL) {
                and(product.saleState.name.eq(saleState))
            }
        }

        // 페이징 쿼리 생성
        val products: List<Product> = queryFactory
            .selectFrom(product)
            .where(builder)
            .orderBy(product.id.desc())
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .fetch()

        // Count 쿼리 생성
        val countQuery: JPAQuery<Long> = queryFactory
            .select(product.count())
            .from(product)
            .where(builder)

        return PageableExecutionUtils.getPage(products, pageable) { countQuery.fetchOne() ?: 0L }
    }
}