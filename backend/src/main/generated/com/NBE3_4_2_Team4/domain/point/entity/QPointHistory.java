package com.NBE3_4_2_Team4.domain.point.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPointHistory is a Querydsl query type for PointHistory
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPointHistory extends EntityPathBase<PointHistory> {

    private static final long serialVersionUID = 1216083430L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPointHistory pointHistory = new QPointHistory("pointHistory");

    public final NumberPath<Long> amount = createNumber("amount", Long.class);

    public final StringPath correlationId = createString("correlationId");

    public final com.NBE3_4_2_Team4.domain.member.member.entity.QMember counterMember;

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final com.NBE3_4_2_Team4.domain.member.member.entity.QMember member;

    public final EnumPath<PointCategory> pointCategory = createEnum("pointCategory", PointCategory.class);

    public QPointHistory(String variable) {
        this(PointHistory.class, forVariable(variable), INITS);
    }

    public QPointHistory(Path<? extends PointHistory> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPointHistory(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPointHistory(PathMetadata metadata, PathInits inits) {
        this(PointHistory.class, metadata, inits);
    }

    public QPointHistory(Class<? extends PointHistory> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.counterMember = inits.isInitialized("counterMember") ? new com.NBE3_4_2_Team4.domain.member.member.entity.QMember(forProperty("counterMember")) : null;
        this.member = inits.isInitialized("member") ? new com.NBE3_4_2_Team4.domain.member.member.entity.QMember(forProperty("member")) : null;
    }

}

