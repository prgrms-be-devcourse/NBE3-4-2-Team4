package com.NBE3_4_2_Team4.domain.board.recommend.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QRecommend is a Querydsl query type for Recommend
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QRecommend extends EntityPathBase<Recommend> {

    private static final long serialVersionUID = -2092174442L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QRecommend recommend = new QRecommend("recommend");

    public final com.NBE3_4_2_Team4.global.jpa.entity.QBaseEntity _super = new com.NBE3_4_2_Team4.global.jpa.entity.QBaseEntity(this);

    //inherited
    public final NumberPath<Long> id = _super.id;

    public final com.NBE3_4_2_Team4.domain.member.member.entity.QMember member;

    public final com.NBE3_4_2_Team4.domain.board.question.entity.QQuestion question;

    public final DateTimePath<java.time.LocalDateTime> recommendAt = createDateTime("recommendAt", java.time.LocalDateTime.class);

    public QRecommend(String variable) {
        this(Recommend.class, forVariable(variable), INITS);
    }

    public QRecommend(Path<? extends Recommend> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QRecommend(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QRecommend(PathMetadata metadata, PathInits inits) {
        this(Recommend.class, metadata, inits);
    }

    public QRecommend(Class<? extends Recommend> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.member = inits.isInitialized("member") ? new com.NBE3_4_2_Team4.domain.member.member.entity.QMember(forProperty("member")) : null;
        this.question = inits.isInitialized("question") ? new com.NBE3_4_2_Team4.domain.board.question.entity.QQuestion(forProperty("question"), inits.get("question")) : null;
    }

}

