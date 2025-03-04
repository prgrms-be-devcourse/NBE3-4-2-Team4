package com.NBE3_4_2_Team4.domain.board.message.entity;

import com.NBE3_4_2_Team4.domain.member.member.entity.Member;
import com.NBE3_4_2_Team4.global.exceptions.ServiceException;
import com.NBE3_4_2_Team4.global.jpa.entity.BaseEntity;
import com.NBE3_4_2_Team4.global.jpa.entity.BaseTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.ManyToOne;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Message extends BaseEntity {
    @ManyToOne
    private Member sender;

    @ManyToOne
    private Member receiver;

    @Column(length = 100)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @CreatedDate
    private LocalDateTime createdAt;

    @Setter
    private boolean isChecked;

    @Setter
    private boolean deletedBySender;

    @Setter
    private boolean deletedByReceiver;

    public void checkSenderCanRead(Member actor) {
        if (!sender.equals(actor)) {
            throw new ServiceException("403-1", "작성자만 쪽지를 읽을 수 있습니다.");
        }
    }

    public void checkReceiverCanRead(Member actor) {
        if (!receiver.equals(actor)) {
            throw new ServiceException("403-2", "수신자만 쪽지를 읽을 수 있습니다.");
        }
    }
}
