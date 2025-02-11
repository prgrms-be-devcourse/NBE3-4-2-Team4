package com.NBE3_4_2_Team4.domain.product.order.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QProductOrder is a Querydsl query type for ProductOrder
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QProductOrder extends EntityPathBase<ProductOrder> {

    private static final long serialVersionUID = -1526392288L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QProductOrder productOrder = new QProductOrder("productOrder");

    public final com.NBE3_4_2_Team4.global.jpa.entity.QBaseEntity _super = new com.NBE3_4_2_Team4.global.jpa.entity.QBaseEntity(this);

    //inherited
    public final NumberPath<Long> id = _super.id;

    public final DateTimePath<java.time.LocalDateTime> orderTime = createDateTime("orderTime", java.time.LocalDateTime.class);

    public final com.NBE3_4_2_Team4.domain.point.entity.QPointHistory pointHistory;

    public final com.NBE3_4_2_Team4.domain.product.product.entity.QProduct product;

    public QProductOrder(String variable) {
        this(ProductOrder.class, forVariable(variable), INITS);
    }

    public QProductOrder(Path<? extends ProductOrder> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QProductOrder(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QProductOrder(PathMetadata metadata, PathInits inits) {
        this(ProductOrder.class, metadata, inits);
    }

    public QProductOrder(Class<? extends ProductOrder> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.pointHistory = inits.isInitialized("pointHistory") ? new com.NBE3_4_2_Team4.domain.point.entity.QPointHistory(forProperty("pointHistory"), inits.get("pointHistory")) : null;
        this.product = inits.isInitialized("product") ? new com.NBE3_4_2_Team4.domain.product.product.entity.QProduct(forProperty("product"), inits.get("product")) : null;
    }

}

