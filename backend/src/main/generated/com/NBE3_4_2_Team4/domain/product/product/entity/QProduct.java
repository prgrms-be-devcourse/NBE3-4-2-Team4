package com.NBE3_4_2_Team4.domain.product.product.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QProduct is a Querydsl query type for Product
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QProduct extends EntityPathBase<Product> {

    private static final long serialVersionUID = 1086840717L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QProduct product = new QProduct("product");

    public final com.NBE3_4_2_Team4.domain.product.category.entity.QProductCategory category;

    public final StringPath description = createString("description");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath imageUrl = createString("imageUrl");

    public final StringPath name = createString("name");

    public final NumberPath<Integer> price = createNumber("price", Integer.class);

    public final ListPath<com.NBE3_4_2_Team4.domain.product.order.entity.ProductOrder, com.NBE3_4_2_Team4.domain.product.order.entity.QProductOrder> productOrders = this.<com.NBE3_4_2_Team4.domain.product.order.entity.ProductOrder, com.NBE3_4_2_Team4.domain.product.order.entity.QProductOrder>createList("productOrders", com.NBE3_4_2_Team4.domain.product.order.entity.ProductOrder.class, com.NBE3_4_2_Team4.domain.product.order.entity.QProductOrder.class, PathInits.DIRECT2);

    public final com.NBE3_4_2_Team4.domain.product.saleState.entity.QProductSaleState saleState;

    public QProduct(String variable) {
        this(Product.class, forVariable(variable), INITS);
    }

    public QProduct(Path<? extends Product> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QProduct(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QProduct(PathMetadata metadata, PathInits inits) {
        this(Product.class, metadata, inits);
    }

    public QProduct(Class<? extends Product> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.category = inits.isInitialized("category") ? new com.NBE3_4_2_Team4.domain.product.category.entity.QProductCategory(forProperty("category"), inits.get("category")) : null;
        this.saleState = inits.isInitialized("saleState") ? new com.NBE3_4_2_Team4.domain.product.saleState.entity.QProductSaleState(forProperty("saleState")) : null;
    }

}

