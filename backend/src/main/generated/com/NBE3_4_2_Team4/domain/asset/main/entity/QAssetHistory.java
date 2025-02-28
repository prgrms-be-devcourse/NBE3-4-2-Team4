package com.NBE3_4_2_Team4.domain.asset.main.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QAssetHistory is a Querydsl query type for AssetHistory
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAssetHistory extends EntityPathBase<AssetHistory> {

    private static final long serialVersionUID = 1322762339L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QAssetHistory assetHistory = new QAssetHistory("assetHistory");

    public final NumberPath<Long> amount = createNumber("amount", Long.class);

    public final EnumPath<AssetCategory> assetCategory = createEnum("assetCategory", AssetCategory.class);

    public final EnumPath<AssetType> assetType = createEnum("assetType", AssetType.class);

    public final StringPath correlationId = createString("correlationId");

    public final com.NBE3_4_2_Team4.domain.member.member.entity.QMember counterMember;

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final com.NBE3_4_2_Team4.domain.member.member.entity.QMember member;

    public QAssetHistory(String variable) {
        this(AssetHistory.class, forVariable(variable), INITS);
    }

    public QAssetHistory(Path<? extends AssetHistory> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QAssetHistory(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QAssetHistory(PathMetadata metadata, PathInits inits) {
        this(AssetHistory.class, metadata, inits);
    }

    public QAssetHistory(Class<? extends AssetHistory> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.counterMember = inits.isInitialized("counterMember") ? new com.NBE3_4_2_Team4.domain.member.member.entity.QMember(forProperty("counterMember"), inits.get("counterMember")) : null;
        this.member = inits.isInitialized("member") ? new com.NBE3_4_2_Team4.domain.member.member.entity.QMember(forProperty("member"), inits.get("member")) : null;
    }

}

