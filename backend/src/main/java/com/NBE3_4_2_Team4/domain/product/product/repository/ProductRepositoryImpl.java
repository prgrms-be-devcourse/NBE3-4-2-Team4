package com.NBE3_4_2_Team4.domain.product.product.repository;

import com.NBE3_4_2_Team4.domain.product.product.entity.Product;
import com.NBE3_4_2_Team4.standard.search.ProductSearchKeywordType;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.NBE3_4_2_Team4.domain.product.product.entity.QProduct.product;

@Repository
public class ProductRepositoryImpl implements ProductRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public ProductRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<Product> findByKeyword(ProductSearchKeywordType keywordType, String keyword, Pageable pageable) {

        BooleanBuilder builder = new BooleanBuilder();

        // 검색 유형에 따라 조건 추가
        if (keywordType == ProductSearchKeywordType.NAME) {
            builder.and(product.name.containsIgnoreCase(keyword));

        } else if (keywordType == ProductSearchKeywordType.CATEGORY) {
            builder.and(product.category.name.containsIgnoreCase(keyword)
                    .or(product.category.parent.name.containsIgnoreCase(keyword)));

        } else {
            builder.and(
                    product.name.containsIgnoreCase(keyword)
                    .or(product.category.name.containsIgnoreCase(keyword))
                            .or(product.category.parent.name.containsIgnoreCase(keyword))
            );
        }

        // 페이징 쿼리 생성
        List<Product> products = queryFactory
                .selectFrom(product)
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // Count 쿼리 생성
        JPAQuery<Long> countQuery = queryFactory
                .select(product.count())
                .from(product)
                .where(builder);

        return PageableExecutionUtils.getPage(products, pageable, countQuery::fetchOne);
    }
}