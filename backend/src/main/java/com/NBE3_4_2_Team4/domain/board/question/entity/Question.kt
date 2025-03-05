package com.NBE3_4_2_Team4.domain.board.question.entity

import com.NBE3_4_2_Team4.domain.asset.main.entity.AssetType
import com.NBE3_4_2_Team4.domain.base.genFile.entity.GenFileParent
import com.NBE3_4_2_Team4.domain.board.answer.entity.Answer
import com.NBE3_4_2_Team4.domain.board.genFile.entity.QuestionGenFile
import com.NBE3_4_2_Team4.domain.board.recommend.entity.Recommend
import com.NBE3_4_2_Team4.domain.member.member.entity.Member
import com.NBE3_4_2_Team4.global.exceptions.ServiceException
import com.NBE3_4_2_Team4.global.rsData.RsData
import com.NBE3_4_2_Team4.standard.base.Empty
import jakarta.persistence.*

@Entity
class Question(
    @ManyToOne
    val author: Member,

    @Column(length = 100)
    var title: String,

    @Column(columnDefinition = "TEXT")
    var questionContent: String,

    @ManyToOne
    var category: QuestionCategory,

    @OneToMany(mappedBy = "question", cascade = [CascadeType.ALL]) // 질문 삭제 시 답변 삭제
    val answers: MutableList<Answer>,

    @OneToMany(mappedBy = "question", cascade = [CascadeType.ALL])
    val recommends: MutableList<Recommend>,

    @OneToOne
    val selectedAnswer: Answer? = null,

    var closed: Boolean, // 질문 상태(답변 추가 가능 여부)

    var amount: Long,

    @Enumerated(EnumType.STRING) // 포인트/캐시 여부
    val assetType: AssetType,

    var rankReceived: Boolean // 랭킹 포인트 지급 여부
) : GenFileParent<QuestionGenFile>(QuestionGenFile::class.java) {
    fun getRecommendCount(): Long = recommends.size.toLong() // 추천 수 반환

    fun modify(title: String, content: String, amount: Long, category: QuestionCategory) {
        this.title = title
        this.questionContent = content
        this.amount = amount
        this.category = category
    }

    fun checkActorCanModify(actor: Member) {
        if (actor != author)
            throw ServiceException("403-1", "게시글 작성자만 수정할 수 있습니다.")
    }

    fun checkActorCanDelete(actor: Member) {
        if (actor.role != Member.Role.ADMIN && actor != author)
            throw ServiceException("403-1", "게시글 작성자만 삭제할 수 있습니다.")
    }

    override fun modify(content: String) {
        this.questionContent = content
    }

    override fun getContent(): String {
        return questionContent
    }

    override fun checkActorCanMakeNewGenFile(actor: Member) {
        getCheckActorCanMakeNewGenFileRs(actor).takeIf { it.isFail }?.let {
            throw ServiceException(it.resultCode, it.msg)
        }
    }

    override fun getCheckActorCanMakeNewGenFileRs(actor: Member?): RsData<Empty> {
        return when (actor) {
            null -> RsData("401-1", "로그인 후 이용해주세요.")
            author -> RsData.OK
            else -> RsData("403-1", "작성자만 파일을 업로드할 수 있습니다.")
        }
    }
}
