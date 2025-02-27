package com.NBE3_4_2_Team4.domain.member.member.entity.asset;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QPoint is a Querydsl query type for Point
 */
@Generated("com.querydsl.codegen.DefaultEmbeddableSerializer")
public class QPoint extends BeanPath<Point> {

    private static final long serialVersionUID = -1575426766L;

    public static final QPoint point = new QPoint("point");

    public final NumberPath<Long> amount = createNumber("amount", Long.class);

    public QPoint(String variable) {
        super(Point.class, forVariable(variable));
    }

    public QPoint(Path<? extends Point> path) {
        super(path.getType(), path.getMetadata());
    }

    public QPoint(PathMetadata metadata) {
        super(Point.class, metadata);
    }

}

