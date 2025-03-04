package com.NBE3_4_2_Team4.domain.board.message.repository;

import com.NBE3_4_2_Team4.domain.board.message.entity.Message;
import com.NBE3_4_2_Team4.domain.member.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findAllByReceiver(Member receiver);
    List<Message> findAllBySender(Member sender);
    List<Message> findAllByReceiverAndIsChecked(Member actor, boolean checked);
}
