package com.NBE3_4_2_Team4.domain.member.member.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMember is a Querydsl query type for Member
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMember extends EntityPathBase<Member> {

    private static final long serialVersionUID = 1119171194L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QMember member = new QMember("member1");

    public final ListPath<com.NBE3_4_2_Team4.domain.board.answer.entity.Answer, com.NBE3_4_2_Team4.domain.board.answer.entity.QAnswer> answers = this.<com.NBE3_4_2_Team4.domain.board.answer.entity.Answer, com.NBE3_4_2_Team4.domain.board.answer.entity.QAnswer>createList("answers", com.NBE3_4_2_Team4.domain.board.answer.entity.Answer.class, com.NBE3_4_2_Team4.domain.board.answer.entity.QAnswer.class, PathInits.DIRECT2);

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final DatePath<java.time.LocalDate> lastAttendanceDate = createDate("lastAttendanceDate", java.time.LocalDate.class);

    public final StringPath nickname = createString("nickname");

    public final EnumPath<Member.OAuth2Provider> oAuth2Provider = createEnum("oAuth2Provider", Member.OAuth2Provider.class);

    public final com.NBE3_4_2_Team4.domain.member.OAuth2RefreshToken.entity.QOAuth2RefreshToken oauth2RefreshToken;

    public final StringPath password = createString("password");

    public final StringPath phoneNumber = createString("phoneNumber");

    public final NumberPath<Long> point = createNumber("point", Long.class);

    public final ListPath<com.NBE3_4_2_Team4.domain.board.question.entity.Question, com.NBE3_4_2_Team4.domain.board.question.entity.QQuestion> questions = this.<com.NBE3_4_2_Team4.domain.board.question.entity.Question, com.NBE3_4_2_Team4.domain.board.question.entity.QQuestion>createList("questions", com.NBE3_4_2_Team4.domain.board.question.entity.Question.class, com.NBE3_4_2_Team4.domain.board.question.entity.QQuestion.class, PathInits.DIRECT2);

    public final EnumPath<Member.Role> role = createEnum("role", Member.Role.class);

    public final StringPath username = createString("username");

    public QMember(String variable) {
        this(Member.class, forVariable(variable), INITS);
    }

    public QMember(Path<? extends Member> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QMember(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QMember(PathMetadata metadata, PathInits inits) {
        this(Member.class, metadata, inits);
    }

    public QMember(Class<? extends Member> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.oauth2RefreshToken = inits.isInitialized("oauth2RefreshToken") ? new com.NBE3_4_2_Team4.domain.member.OAuth2RefreshToken.entity.QOAuth2RefreshToken(forProperty("oauth2RefreshToken"), inits.get("oauth2RefreshToken")) : null;
    }

}

