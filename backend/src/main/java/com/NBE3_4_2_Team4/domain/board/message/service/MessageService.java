package com.NBE3_4_2_Team4.domain.board.message.service;

import com.NBE3_4_2_Team4.domain.board.message.dto.MessageDto;
import com.NBE3_4_2_Team4.domain.board.message.entity.Message;
import com.NBE3_4_2_Team4.domain.board.message.entity.MessageType;
import com.NBE3_4_2_Team4.domain.board.message.repository.MessageRepository;
import com.NBE3_4_2_Team4.domain.member.member.entity.Member;
import com.NBE3_4_2_Team4.domain.member.member.repository.MemberRepository;
import com.NBE3_4_2_Team4.global.exceptions.ServiceException;
import com.NBE3_4_2_Team4.global.security.AuthManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final MessageRepository messageRepository;
    private final MemberRepository memberRepository;

    public long count() {
        return messageRepository.count();
    }

    @Transactional(readOnly = true)
    public List<MessageDto> getSentMessages() {
        return getMessagesByType(MessageType.SENT);
    }

    @Transactional(readOnly = true)
    public List<MessageDto> getReceivedMessages() {
        return getMessagesByType(MessageType.RECEIVED);
    }

    private List<MessageDto> getMessagesByType(MessageType type) {
        Member actor = memberRepository.findByUsername("admin@test.com").get();
        List<Message> messages;

        if (type == MessageType.RECEIVED) {
            messages = messageRepository.findAllByReceiver(actor);
        } else {
            messages = messageRepository.findAllBySender(actor);
        }

        return messages.stream().map(MessageDto::new).toList();
    }

    @Transactional(readOnly = true)
    public MessageDto getMessage(long id) {
        Member actor = AuthManager.getNonNullMember();
        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new ServiceException("404-1", "존재하지 않는 메시지입니다."));

        message.checkSenderCanRead(actor);
        return new MessageDto(message);
    }

    @Transactional
    public MessageDto write(Member sender, String receiverName, String title, String content) {
        Member receiver = memberRepository.findByNickname(receiverName)
                .orElseThrow(() -> new ServiceException("404-1", "존재하지 않는 사용자입니다."));
        Message message = Message.builder()
                .sender(sender)
                .receiver(receiver)
                .title(title)
                .content(content)
                .isChecked(false)
                .build();

        messageRepository.save(message);
        return new MessageDto(message);
    }

    @Transactional
    public void delete(List<Long> ids) {
        Member actor = AuthManager.getNonNullMember();
        List<Message> messages = messageRepository.findAllById(ids);

        messages.forEach(message -> {
                    if (message == null) {
                        throw new ServiceException("404-1", "존재하지 않는 메시지가 포함되어 있습니다.");
                    }
                    message.checkActorCanDelete(actor);
                }
        );

        messageRepository.deleteAll(messages);
    }

    @Transactional
    public void readMessage(List<Long> ids) {
        Member actor = AuthManager.getNonNullMember();
        List<Message> messages = messageRepository.findAllById(ids);

        messages.forEach(message -> {
                    if (message == null) {
                        throw new ServiceException("404-1", "존재하지 않는 메시지가 포함되어 있습니다.");
                    }
                    message.checkReceiverCanRead(actor);
                    message.setChecked(true);
                }
        );
    }
}
