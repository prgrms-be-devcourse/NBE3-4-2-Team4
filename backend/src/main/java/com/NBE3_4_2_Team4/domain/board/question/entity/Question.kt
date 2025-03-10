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
class Question : GenFileParent<QuestionGenFile, Question> {
    @ManyToOne
    lateinit var author: Member

    @Column(length = 100)
    lateinit var title: String

    @Column(columnDefinition = "TEXT")
    override lateinit var content: String

    @ManyToOne
    lateinit var category: QuestionCategory

    @OneToMany(mappedBy = "question", cascade = [CascadeType.ALL]) // 질문 삭제 시 답변 삭제
    var answers: MutableList<Answer> = mutableListOf()

    @OneToMany(mappedBy = "question", cascade = [CascadeType.ALL])
    var recommends: MutableList<Recommend> = mutableListOf()

    @OneToOne
    var selectedAnswer: Answer? = null

    var closed: Boolean = false // 질문 상태(답변 추가 가능 여부)

    var amount: Long = 0

    @Enumerated(EnumType.STRING) // 포인트/캐시 여부
    lateinit var assetType: AssetType

    var rankReceived: Boolean = false // 랭킹 포인트 지급 여부

    constructor() : super(QuestionGenFile::class.java)

    constructor(
        title: String,
        content: String,
        author: Member,
        category: QuestionCategory,
        assetType: AssetType,
        amount: Long
    ) : super(QuestionGenFile::class.java) {
        this.title = title
        this.content = content
        this.author = author
        this.category = category
        this.assetType = assetType
        this.amount = amount
    }
    fun getRecommendCount(): Long = recommends.size.toLong() // 추천 수 반환

    fun modify(title: String, content: String, amount: Long, category: QuestionCategory) {
        this.title = title
        this.content = content
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
        this.content = content
    }

    override fun checkActorCanMakeNewGenFile(actor: Member) {
        getCheckActorCanMakeNewGenFileRs(actor).takeIf { it.isFail }?.let {
            throw ServiceException(it.resultCode, it.msg)
        }
    }

    override fun getCheckActorCanMakeNewGenFileRs(actor: Member): RsData<Empty> {
        return when (actor) {
            null -> RsData("401-1", "로그인 후 이용해주세요.")
            author -> RsData.OK
            else -> RsData("403-1", "작성자만 파일을 업로드할 수 있습니다.")
        }
    }
}
