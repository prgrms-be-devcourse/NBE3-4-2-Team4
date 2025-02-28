package com.NBE3_4_2_Team4.domain.member.member.entity.asset;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QCash is a Querydsl query type for Cash
 */
@Generated("com.querydsl.codegen.DefaultEmbeddableSerializer")
public class QCash extends BeanPath<Cash> {

    private static final long serialVersionUID = -51220655L;

    public static final QCash cash = new QCash("cash");

    public final NumberPath<Long> amount = createNumber("amount", Long.class);

    public QCash(String variable) {
        super(Cash.class, forVariable(variable));
    }

    public QCash(Path<? extends Cash> path) {
        super(path.getType(), path.getMetadata());
    }

    public QCash(PathMetadata metadata) {
        super(Cash.class, metadata);
    }

}

