package com.NBE3_4_2_Team4.domain.board.genFile.entity

import com.NBE3_4_2_Team4.domain.base.genFile.entity.GenFile
import com.NBE3_4_2_Team4.domain.board.question.entity.Question
import jakarta.persistence.Entity

@Entity
class QuestionGenFile : GenFile<Question>()
