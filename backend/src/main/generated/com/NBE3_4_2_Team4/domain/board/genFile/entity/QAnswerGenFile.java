package com.NBE3_4_2_Team4.domain.board.genFile.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QAnswerGenFile is a Querydsl query type for AnswerGenFile
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAnswerGenFile extends EntityPathBase<AnswerGenFile> {

    private static final long serialVersionUID = -990358600L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QAnswerGenFile answerGenFile = new QAnswerGenFile("answerGenFile");

    public final com.NBE3_4_2_Team4.domain.base.genFile.entity.QGenFile _super = new com.NBE3_4_2_Team4.domain.base.genFile.entity.QGenFile(this);

    public final com.NBE3_4_2_Team4.domain.board.answer.entity.QAnswer answer;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final StringPath fileDateDir = _super.fileDateDir;

    //inherited
    public final StringPath fileExt = _super.fileExt;

    //inherited
    public final StringPath fileExtType2Code = _super.fileExtType2Code;

    //inherited
    public final StringPath fileExtTypeCode = _super.fileExtTypeCode;

    //inherited
    public final StringPath fileName = _super.fileName;

    //inherited
    public final NumberPath<Integer> fileNo = _super.fileNo;

    //inherited
    public final NumberPath<Integer> fileSize = _super.fileSize;

    //inherited
    public final NumberPath<Long> id = _super.id;

    //inherited
    public final StringPath metadata = _super.metadata;

    //inherited
    public final StringPath originalFileName = _super.originalFileName;

    public final EnumPath<AnswerGenFile.TypeCode> typeCode = createEnum("typeCode", AnswerGenFile.TypeCode.class);

    public QAnswerGenFile(String variable) {
        this(AnswerGenFile.class, forVariable(variable), INITS);
    }

    public QAnswerGenFile(Path<? extends AnswerGenFile> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QAnswerGenFile(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QAnswerGenFile(PathMetadata metadata, PathInits inits) {
        this(AnswerGenFile.class, metadata, inits);
    }

    public QAnswerGenFile(Class<? extends AnswerGenFile> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.answer = inits.isInitialized("answer") ? new com.NBE3_4_2_Team4.domain.board.answer.entity.QAnswer(forProperty("answer"), inits.get("answer")) : null;
    }

}

