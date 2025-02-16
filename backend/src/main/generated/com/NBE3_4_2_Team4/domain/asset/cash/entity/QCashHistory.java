package com.NBE3_4_2_Team4.domain.asset.cash.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QCashHistory is a Querydsl query type for CashHistory
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCashHistory extends EntityPathBase<CashHistory> {

    private static final long serialVersionUID = -999804568L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QCashHistory cashHistory = new QCashHistory("cashHistory");

    public final NumberPath<Long> amount = createNumber("amount", Long.class);

    public final EnumPath<com.NBE3_4_2_Team4.domain.asset.main.entity.AssetCategory> assetCategory = createEnum("assetCategory", com.NBE3_4_2_Team4.domain.asset.main.entity.AssetCategory.class);

    public final com.NBE3_4_2_Team4.domain.member.member.entity.QMember counterMember;

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final com.NBE3_4_2_Team4.domain.member.member.entity.QMember member;

    public QCashHistory(String variable) {
        this(CashHistory.class, forVariable(variable), INITS);
    }

    public QCashHistory(Path<? extends CashHistory> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QCashHistory(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QCashHistory(PathMetadata metadata, PathInits inits) {
        this(CashHistory.class, metadata, inits);
    }

    public QCashHistory(Class<? extends CashHistory> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.counterMember = inits.isInitialized("counterMember") ? new com.NBE3_4_2_Team4.domain.member.member.entity.QMember(forProperty("counterMember"), inits.get("counterMember")) : null;
        this.member = inits.isInitialized("member") ? new com.NBE3_4_2_Team4.domain.member.member.entity.QMember(forProperty("member"), inits.get("member")) : null;
    }

}

