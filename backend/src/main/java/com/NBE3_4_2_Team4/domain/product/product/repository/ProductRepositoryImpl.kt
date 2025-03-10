package com.NBE3_4_2_Team4.domain.product.product.repository;

import com.NBE3_4_2_Team4.domain.product.product.entity.Product;
import com.NBE3_4_2_Team4.domain.product.saleState.entity.SaleState;
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
    public Page<Product> findByKeyword(
            ProductSearchKeywordType searchKeywordType,
            String searchKeyword,
            String categoryKeyword,
            SaleState saleState,
            Pageable pageable
    ) {

        BooleanBuilder builder = new BooleanBuilder();

        // 검색 유형에 따라 조건 추가
        if (searchKeywordType == ProductSearchKeywordType.NAME) {
            builder.and(product.name.containsIgnoreCase(searchKeyword));

        } else if (searchKeywordType == ProductSearchKeywordType.CATEGORY) {
            builder.and(product.category.name.containsIgnoreCase(searchKeyword)
                    .or(product.category.parent.name.containsIgnoreCase(searchKeyword)));

        } else {
            builder.and(
                    product.name.containsIgnoreCase(searchKeyword)
                    .or(product.category.name.containsIgnoreCase(searchKeyword))
                            .or(product.category.parent.name.containsIgnoreCase(searchKeyword))
            );
        }

        // 카테고리에 따라 조건 추가
        if (categoryKeyword != null && !categoryKeyword.isEmpty()) {
            builder.and(product.category.name.containsIgnoreCase(categoryKeyword)
                    .or(product.category.parent.name.containsIgnoreCase(categoryKeyword)));
        }

        // 판매 상태에 따라 조건 추가
        if (saleState != SaleState.ALL) {
            builder.and(product.saleState.name.eq(saleState));
        }

        // 페이징 쿼리 생성
        List<Product> products = queryFactory
                .selectFrom(product)
                .where(builder)
                .orderBy(product._id.desc())
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