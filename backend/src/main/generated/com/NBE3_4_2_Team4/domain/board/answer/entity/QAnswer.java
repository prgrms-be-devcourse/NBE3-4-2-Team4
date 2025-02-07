package com.NBE3_4_2_Team4.domain.board.answer.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QAnswer is a Querydsl query type for Answer
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAnswer extends EntityPathBase<Answer> {

    private static final long serialVersionUID = 1811599532L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QAnswer answer = new QAnswer("answer");

    public final com.NBE3_4_2_Team4.global.jpa.entity.QBaseTime _super = new com.NBE3_4_2_Team4.global.jpa.entity.QBaseTime(this);

    public final com.NBE3_4_2_Team4.domain.member.member.entity.QMember author;

    public final StringPath content = createString("content");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final NumberPath<Long> id = _super.id;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedAt = _super.modifiedAt;

    public final com.NBE3_4_2_Team4.domain.board.question.entity.QQuestion question;

    public final BooleanPath selected = createBoolean("selected");

    public final DateTimePath<java.time.LocalDateTime> selectedAt = createDateTime("selectedAt", java.time.LocalDateTime.class);

    public QAnswer(String variable) {
        this(Answer.class, forVariable(variable), INITS);
    }

    public QAnswer(Path<? extends Answer> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QAnswer(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QAnswer(PathMetadata metadata, PathInits inits) {
        this(Answer.class, metadata, inits);
    }

    public QAnswer(Class<? extends Answer> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.author = inits.isInitialized("author") ? new com.NBE3_4_2_Team4.domain.member.member.entity.QMember(forProperty("author"), inits.get("author")) : null;
        this.question = inits.isInitialized("question") ? new com.NBE3_4_2_Team4.domain.board.question.entity.QQuestion(forProperty("question"), inits.get("question")) : null;
    }

}

