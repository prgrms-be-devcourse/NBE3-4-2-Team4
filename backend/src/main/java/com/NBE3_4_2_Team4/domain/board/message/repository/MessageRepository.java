package com.NBE3_4_2_Team4.domain.board.message.repository;

import com.NBE3_4_2_Team4.domain.board.message.entity.Message;
import com.NBE3_4_2_Team4.domain.member.member.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    Page<Message> findByReceiverIdAndDeletedByReceiverFalseOrderByCreatedAtDesc(Pageable pageable, Long receiverId);

    Page<Message> findBySenderIdAndDeletedBySenderFalseOrderByCreatedAtDesc(Pageable pageable, Long senderId);

    List<Message> findAllByReceiverAndChecked(Member actor, boolean checked);
}
