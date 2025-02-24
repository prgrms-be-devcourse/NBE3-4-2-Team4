package com.NBE3_4_2_Team4.domain.board.question.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QQuestion is a Querydsl query type for Question
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QQuestion extends EntityPathBase<Question> {

    private static final long serialVersionUID = -775828292L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QQuestion question = new QQuestion("question");

    public final com.NBE3_4_2_Team4.domain.base.genFile.entity.QGenFileParent _super = new com.NBE3_4_2_Team4.domain.base.genFile.entity.QGenFileParent(this);

    public final NumberPath<Long> amount = createNumber("amount", Long.class);

    public final ListPath<com.NBE3_4_2_Team4.domain.board.answer.entity.Answer, com.NBE3_4_2_Team4.domain.board.answer.entity.QAnswer> answers = this.<com.NBE3_4_2_Team4.domain.board.answer.entity.Answer, com.NBE3_4_2_Team4.domain.board.answer.entity.QAnswer>createList("answers", com.NBE3_4_2_Team4.domain.board.answer.entity.Answer.class, com.NBE3_4_2_Team4.domain.board.answer.entity.QAnswer.class, PathInits.DIRECT2);

    public final EnumPath<com.NBE3_4_2_Team4.domain.asset.main.entity.AssetType> assetType = createEnum("assetType", com.NBE3_4_2_Team4.domain.asset.main.entity.AssetType.class);

    public final com.NBE3_4_2_Team4.domain.member.member.entity.QMember author;

    public final QQuestionCategory category;

    public final BooleanPath closed = createBoolean("closed");

    public final StringPath content = createString("content");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final ListPath<com.NBE3_4_2_Team4.domain.base.genFile.entity.GenFile<?>, com.NBE3_4_2_Team4.domain.base.genFile.entity.QGenFile> genFiles = _super.genFiles;

    //inherited
    public final NumberPath<Long> id = _super.id;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedAt = _super.modifiedAt;

    public final BooleanPath rankReceived = createBoolean("rankReceived");

    public final ListPath<com.NBE3_4_2_Team4.domain.board.recommend.entity.Recommend, com.NBE3_4_2_Team4.domain.board.recommend.entity.QRecommend> recommends = this.<com.NBE3_4_2_Team4.domain.board.recommend.entity.Recommend, com.NBE3_4_2_Team4.domain.board.recommend.entity.QRecommend>createList("recommends", com.NBE3_4_2_Team4.domain.board.recommend.entity.Recommend.class, com.NBE3_4_2_Team4.domain.board.recommend.entity.QRecommend.class, PathInits.DIRECT2);

    public final com.NBE3_4_2_Team4.domain.board.answer.entity.QAnswer selectedAnswer;

    public final StringPath title = createString("title");

    public QQuestion(String variable) {
        this(Question.class, forVariable(variable), INITS);
    }

    public QQuestion(Path<? extends Question> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QQuestion(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QQuestion(PathMetadata metadata, PathInits inits) {
        this(Question.class, metadata, inits);
    }

    public QQuestion(Class<? extends Question> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.author = inits.isInitialized("author") ? new com.NBE3_4_2_Team4.domain.member.member.entity.QMember(forProperty("author"), inits.get("author")) : null;
        this.category = inits.isInitialized("category") ? new QQuestionCategory(forProperty("category")) : null;
        this.selectedAnswer = inits.isInitialized("selectedAnswer") ? new com.NBE3_4_2_Team4.domain.board.answer.entity.QAnswer(forProperty("selectedAnswer"), inits.get("selectedAnswer")) : null;
    }

}

