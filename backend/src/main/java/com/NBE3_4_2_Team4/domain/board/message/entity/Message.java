package com.NBE3_4_2_Team4.domain.board.message.entity;

import com.NBE3_4_2_Team4.domain.member.member.entity.Member;
import com.NBE3_4_2_Team4.global.exceptions.ServiceException;
import com.NBE3_4_2_Team4.global.jpa.entity.BaseTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.*;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Message extends BaseTime {
    @ManyToOne
    private Member sender;

    @ManyToOne
    private Member receiver;

    @Setter @Column(length = 200)
    private String content;

    @Setter
    private boolean isChecked;

    public void checkSenderCanRead(Member actor) {
        if (!sender.equals(actor)) {
            throw new ServiceException("403-1", "작성자만 쪽지를 읽을 수 있습니다.");
        }
    }

    public void checkActorCanDelete(Member actor) {
        if (!sender.equals(actor)) {
            throw new ServiceException("403-2", "작성자만 쪽지를 삭제할 수 있습니다.");
        }
    }

    public void checkSenderCanModify(Member actor) {
        if (!sender.equals(actor)) {
            throw new ServiceException("403-3", "작성자만 쪽지를 수정할 수 있습니다.");
        }
    }

    public void checkReceiverCanRead(Member actor) {
        if (!receiver.equals(actor)) {
            throw new ServiceException("403-4", "받는 사람만 쪽지를 읽을 수 있습니다.");
        }
    }
}
