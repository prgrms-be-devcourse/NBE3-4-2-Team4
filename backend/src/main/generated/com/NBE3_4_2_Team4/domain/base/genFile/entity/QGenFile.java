package com.NBE3_4_2_Team4.domain.base.genFile.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QGenFile is a Querydsl query type for GenFile
 */
@Generated("com.querydsl.codegen.DefaultSupertypeSerializer")
public class QGenFile extends EntityPathBase<GenFile> {

    private static final long serialVersionUID = -8500331L;

    public static final QGenFile genFile = new QGenFile("genFile");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final StringPath fileDateDir = createString("fileDateDir");

    public final StringPath fileExt = createString("fileExt");

    public final StringPath fileExtType2Code = createString("fileExtType2Code");

    public final StringPath fileExtTypeCode = createString("fileExtTypeCode");

    public final StringPath fileName = createString("fileName");

    public final NumberPath<Integer> fileNo = createNumber("fileNo", Integer.class);

    public final NumberPath<Integer> fileSize = createNumber("fileSize", Integer.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath metadata = createString("metadata");

    public final StringPath originalFileName = createString("originalFileName");

    public QGenFile(String variable) {
        super(GenFile.class, forVariable(variable));
    }

    public QGenFile(Path<? extends GenFile> path) {
        super(path.getType(), path.getMetadata());
    }

    public QGenFile(PathMetadata metadata) {
        super(GenFile.class, metadata);
    }

}

