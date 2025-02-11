package com.NBE3_4_2_Team4.domain.member.OAuth2RefreshToken.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QOAuth2RefreshToken is a Querydsl query type for OAuth2RefreshToken
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QOAuth2RefreshToken extends EntityPathBase<OAuth2RefreshToken> {

    private static final long serialVersionUID = 1946979032L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QOAuth2RefreshToken oAuth2RefreshToken = new QOAuth2RefreshToken("oAuth2RefreshToken");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final com.NBE3_4_2_Team4.domain.member.member.entity.QMember member;

    public final StringPath refreshToken = createString("refreshToken");

    public QOAuth2RefreshToken(String variable) {
        this(OAuth2RefreshToken.class, forVariable(variable), INITS);
    }

    public QOAuth2RefreshToken(Path<? extends OAuth2RefreshToken> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QOAuth2RefreshToken(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QOAuth2RefreshToken(PathMetadata metadata, PathInits inits) {
        this(OAuth2RefreshToken.class, metadata, inits);
    }

    public QOAuth2RefreshToken(Class<? extends OAuth2RefreshToken> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.member = inits.isInitialized("member") ? new com.NBE3_4_2_Team4.domain.member.member.entity.QMember(forProperty("member"), inits.get("member")) : null;
    }

}

