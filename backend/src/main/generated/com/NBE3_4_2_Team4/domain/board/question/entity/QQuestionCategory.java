package com.NBE3_4_2_Team4.domain.board.question.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QQuestionCategory is a Querydsl query type for QuestionCategory
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QQuestionCategory extends EntityPathBase<QuestionCategory> {

    private static final long serialVersionUID = -229346854L;

    public static final QQuestionCategory questionCategory = new QQuestionCategory("questionCategory");

    public final com.NBE3_4_2_Team4.global.jpa.entity.QBaseEntity _super = new com.NBE3_4_2_Team4.global.jpa.entity.QBaseEntity(this);

    //inherited
    public final NumberPath<Long> id = _super.id;

    public final StringPath name = createString("name");

    public QQuestionCategory(String variable) {
        super(QuestionCategory.class, forVariable(variable));
    }

    public QQuestionCategory(Path<? extends QuestionCategory> path) {
        super(path.getType(), path.getMetadata());
    }

    public QQuestionCategory(PathMetadata metadata) {
        super(QuestionCategory.class, metadata);
    }

}

