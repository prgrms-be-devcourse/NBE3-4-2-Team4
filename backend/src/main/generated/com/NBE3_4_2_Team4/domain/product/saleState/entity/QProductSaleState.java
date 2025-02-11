package com.NBE3_4_2_Team4.domain.product.saleState.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QProductSaleState is a Querydsl query type for ProductSaleState
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QProductSaleState extends EntityPathBase<ProductSaleState> {

    private static final long serialVersionUID = 1301206168L;

    public static final QProductSaleState productSaleState = new QProductSaleState("productSaleState");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final EnumPath<SaleState> name = createEnum("name", SaleState.class);

    public final ListPath<com.NBE3_4_2_Team4.domain.product.product.entity.Product, com.NBE3_4_2_Team4.domain.product.product.entity.QProduct> products = this.<com.NBE3_4_2_Team4.domain.product.product.entity.Product, com.NBE3_4_2_Team4.domain.product.product.entity.QProduct>createList("products", com.NBE3_4_2_Team4.domain.product.product.entity.Product.class, com.NBE3_4_2_Team4.domain.product.product.entity.QProduct.class, PathInits.DIRECT2);

    public QProductSaleState(String variable) {
        super(ProductSaleState.class, forVariable(variable));
    }

    public QProductSaleState(Path<? extends ProductSaleState> path) {
        super(path.getType(), path.getMetadata());
    }

    public QProductSaleState(PathMetadata metadata) {
        super(ProductSaleState.class, metadata);
    }

}

