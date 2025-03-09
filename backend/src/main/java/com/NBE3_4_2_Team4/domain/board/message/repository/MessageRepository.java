package com.NBE3_4_2_Team4.domain.board.message.repository;

import com.NBE3_4_2_Team4.domain.board.message.entity.Message;
import com.NBE3_4_2_Team4.domain.member.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByReceiverIdAndDeletedByReceiverFalseOrderByCreatedAtDesc(Long receiverId);

    List<Message> findBySenderIdAndDeletedBySenderFalseOrderByCreatedAtDesc(Long senderId);

    List<Message> findAllByReceiverAndChecked(Member actor, boolean checked);
}
