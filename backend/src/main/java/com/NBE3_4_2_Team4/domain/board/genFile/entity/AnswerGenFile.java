package com.NBE3_4_2_Team4.domain.board.genFile.entity;

import com.NBE3_4_2_Team4.domain.base.genFile.entity.GenFile;
import com.NBE3_4_2_Team4.domain.board.answer.entity.Answer;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class AnswerGenFile extends GenFile<Answer> {}
