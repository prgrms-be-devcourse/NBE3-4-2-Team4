package com.NBE3_4_2_Team4.domain.board.question.entity;

import com.NBE3_4_2_Team4.domain.base.genFile.entity.GenFileParent;
import com.NBE3_4_2_Team4.domain.asset.main.entity.AssetType;
import com.NBE3_4_2_Team4.domain.board.answer.entity.Answer;
import com.NBE3_4_2_Team4.domain.board.genFile.entity.QuestionGenFile;
import com.NBE3_4_2_Team4.domain.board.recommend.entity.Recommend;
import com.NBE3_4_2_Team4.domain.board.search.entity.NewsSearchResult;
import com.NBE3_4_2_Team4.domain.member.member.entity.Member;
import com.NBE3_4_2_Team4.global.exceptions.ServiceException;
import com.NBE3_4_2_Team4.global.rsData.RsData;
import com.NBE3_4_2_Team4.standard.base.Empty;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Optional;

@Entity
@Getter
@Builder
public class Question extends GenFileParent<QuestionGenFile> {
    @ManyToOne
    private Member author;

    @Column(length = 100)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @ManyToOne
    private QuestionCategory category;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL) // 질문 삭제 시 답변 삭제
    private List<Answer> answers;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL)
    private List<Recommend> recommends;

    @OneToOne @Setter
    private Answer selectedAnswer;

    @Setter
    private boolean closed; //질문 상태(답변 추가 가능 여부)

    private long amount;

    @Enumerated(EnumType.STRING) // 포인트/캐시 여부
    private AssetType assetType;

    @Setter
    private boolean rankReceived; // 랭킹 포인트 지급 여부

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "question_search", joinColumns = @JoinColumn(name = "question_id"))
    private List<NewsSearchResult> articles;

    public Question() {
        super(QuestionGenFile.class);
    }

    public Question(Member author, String title, String content, QuestionCategory category, List<Answer> answers, List<Recommend> recommends, Answer selectedAnswer, boolean closed, long amount, AssetType assetType, boolean rankReceived, List<NewsSearchResult> articles) {
        super(QuestionGenFile.class);
        this.author = author;
        this.title = title;
        this.content = content;
        this.category = category;
        this.answers = answers;
        this.recommends = recommends;
        this.selectedAnswer = selectedAnswer;
        this.closed = closed;
        this.amount = amount;
        this.assetType = assetType;
        this.rankReceived = rankReceived;
        this.articles = articles;
    }

    public long getRecommendCount() { // 추천 수 반환
        return recommends == null ? 0 : recommends.size();
    }

    public void modify(String title, String content, long amount, QuestionCategory category) {
        this.title = title;
        this.content = content;
        this.amount = amount;
        this.category = category;
    }

    public void checkActorCanModify(Member actor) {
        if (!actor.equals(this.getAuthor()))
            throw new ServiceException("403-1", "게시글 작성자만 수정할 수 있습니다.");
    }

    public void checkActorCanDelete(Member actor) {
        if (actor.getRole() != Member.Role.ADMIN && !actor.equals(this.getAuthor()))
            throw new ServiceException("403-1", "게시글 작성자만 삭제할 수 있습니다.");
    }

    @Override
    public void modify(String content) {
        this.content = content;
    }

    @Override
    public void checkActorCanMakeNewGenFile(Member actor) {
        Optional.of(
                        getCheckActorCanMakeNewGenFileRs(actor)
                )
                .filter(RsData::isFail)
                .ifPresent(rsData -> {
                    throw new ServiceException(rsData.getResultCode(), rsData.getMsg());
                });
    }

    @Override
    protected RsData<Empty> getCheckActorCanMakeNewGenFileRs(Member actor) {
        if (actor == null) return new RsData<>("401-1", "로그인 후 이용해주세요.");
        if (actor.equals(author)) return RsData.OK;
        return new RsData<>("403-1", "작성자만 파일을 업로드할 수 있습니다.");
    }
}
