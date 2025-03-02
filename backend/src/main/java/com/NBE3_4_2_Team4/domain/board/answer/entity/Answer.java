package com.NBE3_4_2_Team4.domain.board.answer.entity;

import com.NBE3_4_2_Team4.domain.base.genFile.entity.GenFileParent;
import com.NBE3_4_2_Team4.domain.board.genFile.entity.AnswerGenFile;
import com.NBE3_4_2_Team4.domain.board.question.entity.Question;
import com.NBE3_4_2_Team4.domain.member.member.entity.Member;
import com.NBE3_4_2_Team4.global.exceptions.ServiceException;
import com.NBE3_4_2_Team4.global.rsData.RsData;
import com.NBE3_4_2_Team4.standard.base.Empty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Optional;

@Entity
@Getter
public class Answer extends GenFileParent<AnswerGenFile> {
    @ManyToOne(fetch = FetchType.LAZY)
    private Question question;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member author;

    @Column(columnDefinition = "TEXT")
    private String content;

    private boolean selected;

    private LocalDateTime selectedAt;

    public Answer() {
        super(AnswerGenFile.class);
    }

    public Answer(Question question, Member author, String content, boolean selected, LocalDateTime selectedAt) {
        super(AnswerGenFile.class);
        this.question = question;
        this.author = author;
        this.content = content;
        this.selected = selected;
        this.selectedAt = selectedAt;
    }

    public Answer(Question question, Member author, String content) {
        this(question, author, content, false, null);
    }

    public void checkActorCanModify(Member actor) {
        if (!actor.equals(this.getAuthor()))
            throw new ServiceException("403-2", "작성자만 답변을 수정할 수 있습니다.");
    }

    public void checkActorCanDelete(Member actor) {
        if (actor.getRole() != Member.Role.ADMIN && !actor.equals(this.getAuthor()))
            throw new ServiceException("403-2", "작성자만 답변을 삭제할 수 있습니다.");
    }

    @Override
    public void modify(String content) {
        this.content = content;
    }

    public void select() {
        this.selected = true;
        this.setSelectedAt();
    }

    public void setSelectedAt() {
        this.selectedAt = LocalDateTime.now();
    }

    public void setAuthor(Member author) {
        this.author = author;
    }

    public void setContent(String content) {
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
