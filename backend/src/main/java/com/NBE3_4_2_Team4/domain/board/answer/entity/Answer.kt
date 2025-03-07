package com.NBE3_4_2_Team4.domain.board.answer.entity

import com.NBE3_4_2_Team4.domain.base.genFile.entity.GenFileParent
import com.NBE3_4_2_Team4.domain.board.genFile.entity.AnswerGenFile
import com.NBE3_4_2_Team4.domain.board.question.entity.Question
import com.NBE3_4_2_Team4.domain.member.member.entity.Member
import com.NBE3_4_2_Team4.global.exceptions.ServiceException
import com.NBE3_4_2_Team4.global.rsData.RsData
import com.NBE3_4_2_Team4.standard.base.Empty
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.ManyToOne
import java.time.LocalDateTime
import java.util.*

@Entity
class Answer : GenFileParent<AnswerGenFile, Answer> {
    @ManyToOne //Todo : (fetch = FetchType.LAZY)
    lateinit var question: Question

    @ManyToOne //Todo : (fetch = FetchType.LAZY)
    lateinit var author: Member

    @Column(columnDefinition = "TEXT")
    override lateinit var content: String

    var selected = false

    var selectedAt: LocalDateTime? = null

    constructor() : super(AnswerGenFile::class.java)

    constructor(
        question: Question,
        author: Member,
        content: String
    ) : super(
        AnswerGenFile::class.java
    ) {
        this.question = question
        this.author = author
        this.content = content
    }

    fun checkActorCanModify(actor: Member) {
        if (actor != this.author) throw ServiceException("403-2", "작성자만 답변을 수정할 수 있습니다.")
    }

    fun checkActorCanDelete(actor: Member) {
        if (actor.role != Member.Role.ADMIN && actor != this.author) throw ServiceException(
            "403-2",
            "작성자만 답변을 삭제할 수 있습니다."
        )
    }

    override fun modify(content: String) {
        this.content = content
    }

    fun isSelected(): Boolean {
        return selected
    }

    fun select() {
        this.selected = true
        this.selectedAt = LocalDateTime.now()
    }

    override fun checkActorCanMakeNewGenFile(actor: Member) {
        Optional.of(
            getCheckActorCanMakeNewGenFileRs(actor)
        )
            .filter { obj: RsData<Empty> -> obj.isFail }
            .ifPresent { rsData: RsData<Empty> ->
                throw ServiceException(rsData.resultCode, rsData.msg)
            }
    }

    override fun getCheckActorCanMakeNewGenFileRs(actor: Member): RsData<Empty> {
        if (actor == null) return RsData("401-1", "로그인 후 이용해주세요.")
        if (actor == author) return RsData.OK
        return RsData("403-1", "작성자만 파일을 업로드할 수 있습니다.")
    }
}
