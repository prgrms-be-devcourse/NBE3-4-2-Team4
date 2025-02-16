package com.NBE3_4_2_Team4.domain.board.genFile.entity;

import com.NBE3_4_2_Team4.domain.base.genFile.entity.GenFile;
import com.NBE3_4_2_Team4.domain.board.answer.entity.Answer;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class AnswerGenFile extends GenFile {
    public enum TypeCode {
        attachment,
        body
    }

    @ManyToOne(fetch = FetchType.LAZY)
    private Answer answer;

    @Enumerated(EnumType.STRING)
    private TypeCode typeCode;

    @Override
    protected long getOwnerModelId() {
        return answer.getId();
    }

    @Override
    protected String getTypeCodeAsStr() {
        return typeCode.name();
    }
}
