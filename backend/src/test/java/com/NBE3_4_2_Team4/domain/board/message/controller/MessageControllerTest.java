package com.NBE3_4_2_Team4.domain.board.message.controller;

import com.NBE3_4_2_Team4.domain.board.message.dto.MessageDto;
import com.NBE3_4_2_Team4.domain.board.message.dto.request.MessageWriteReqDto;
import com.NBE3_4_2_Team4.domain.board.message.entity.Message;
import com.NBE3_4_2_Team4.domain.board.message.repository.MessageRepository;
import com.NBE3_4_2_Team4.domain.member.member.entity.Member;
import com.NBE3_4_2_Team4.global.security.AuthManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
public class MessageControllerTest {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MessageRepository messageRepository;

    @Test
    @DisplayName("내가 보낸 쪽지 목록 조회")
    @WithUserDetails("test@test.com")
    void t1_1() throws Exception {
        ResultActions resultActions = mvc.perform(
                get("/api/messages/send")
        ).andDo(print());

        Member author = AuthManager.getMemberFromContext();
        List<MessageDto> messages = messageRepository.findAllBySender(author)
                .stream()
                .map(MessageDto::new)
                .toList();

        resultActions.andExpect(handler().handlerType(MessageController.class))
                .andExpect(handler().methodName("getSentMessages"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[*].sender_name").value(everyItem(is("테스트 유저"))));

        for(int i = 0; i < messages.size(); i++) {
            MessageDto message = messages.get(i);

            resultActions
                    .andExpect(jsonPath("$[%d].created_at".formatted(i)).exists())
                    .andExpect(jsonPath("$[%d].title".formatted(i)).value(message.getTitle()))
                    .andExpect(jsonPath("$[%d].content".formatted(i)).value(message.getContent()))
                    .andExpect(jsonPath("$[%d].sender_name".formatted(i)).value(message.getSenderName()))
                    .andExpect(jsonPath("$[%d].receiver_name".formatted(i)).value(message.getReceiverName()))
                    .andExpect(jsonPath("$[%d].checked".formatted(i)).value(message.isChecked()));
        }
    }

    @Test
    @DisplayName("내가 받은 쪽지 목록 조회")
    @WithUserDetails("test@test.com")
    void t1_2() throws Exception {
        Member author = AuthManager.getNonNullMember();

        ResultActions resultActions = mvc.perform(
                get("/api/messages/receive")
        ).andDo(print());

        List<MessageDto> messages = messageRepository.findAllByReceiver(author)
                .stream()
                .map(MessageDto::new)
                .toList();

        resultActions.andExpect(handler().handlerType(MessageController.class))
                .andExpect(handler().methodName("getReceivedMessages"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(4))
                .andExpect(jsonPath("$[*].receiver_name").value(everyItem(is("테스트 유저"))));

        for(int i = 0; i < messages.size(); i++) {
            MessageDto message = messages.get(i);

            resultActions
                    .andExpect(jsonPath("$[%d].title".formatted(i)).value(message.getTitle()))
                    .andExpect(jsonPath("$[%d].content".formatted(i)).value(message.getContent()))
                    .andExpect(jsonPath("$[%d].sender_name".formatted(i)).value(message.getSenderName()))
                    .andExpect(jsonPath("$[%d].receiver_name".formatted(i)).value(message.getReceiverName()))
                    .andExpect(jsonPath("$[%d].created_at".formatted(i)).exists())
                    .andExpect(jsonPath("$[%d].checked".formatted(i)).value(message.isChecked()));
        }
    }

    @Test
    @DisplayName("쪽지 단건조회")
    @WithUserDetails("test@test.com")
    void t2_1() throws Exception {
        ResultActions resultActions = mvc.perform(
                get("/api/messages/1")
        ).andDo(print());

        MessageDto message = new MessageDto(messageRepository.findById(1L).get());

        resultActions.andExpect(handler().handlerType(MessageController.class))
                .andExpect(handler().methodName("getMessage"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(message.getTitle()))
                .andExpect(jsonPath("$.content").value(message.getContent()))
                .andExpect(jsonPath("$.sender_name").value(message.getSenderName()))
                .andExpect(jsonPath("$.receiver_name").value(message.getReceiverName()))
                .andExpect(jsonPath("$.created_at").exists())
                .andExpect(jsonPath("$.checked").value(message.isChecked()));
    }

    @Test
    @DisplayName("쪽지 작성")
    @WithUserDetails("admin@test.com")
    void t3() throws Exception {
        MessageWriteReqDto request = new MessageWriteReqDto("쪽지 제목", "쪽지 내용", "테스트 유저");
        String requestJson = objectMapper.writeValueAsString(request);

        ResultActions resultActions = mvc.perform(
                post("/api/messages")
                        .content(requestJson)
                        .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
        ).andDo(print());

        resultActions.andExpect(handler().handlerType(MessageController.class))
                .andExpect(handler().methodName("write"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.result_code").value("201-1"))
                .andExpect(jsonPath("$.msg").value("쪽지를 성공적으로 보냈습니다."))
                .andExpect(jsonPath("$.data.title").value("쪽지 제목"))
                .andExpect(jsonPath("$.data.content").value("쪽지 내용"))
                .andExpect(jsonPath("$.data.sender_name").value("관리자"))
                .andExpect(jsonPath("$.data.receiver_name").value("테스트 유저"))
                .andExpect(jsonPath("$.data.created_at").exists())
                .andExpect(jsonPath("$.data.checked").value(false));
    }

    @Test
    @DisplayName("내가 받은 쪽지 삭제")
    @WithUserDetails("test@test.com")
    void t4_1() throws Exception {
        List<Long> ids = List.of(1L, 3L);

        ResultActions resultActions = mvc.perform(
                delete("/api/messages")
                        .content(new ObjectMapper().writeValueAsString(ids))
                        .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
        ).andDo(print());

        resultActions.andExpect(handler().handlerType(MessageController.class))
                .andExpect(handler().methodName("delete"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result_code").value("200-2"))
                .andExpect(jsonPath("$.msg").value("%d개의 쪽지를 삭제하였습니다.".formatted(ids.size())));

        List<Message> messages = messageRepository.findAllById(ids);
        assertThat(messages).isEmpty();
    }

    @Test
    @DisplayName("내가 받지 않은 쪽지는 삭제 불가")
    @WithUserDetails("test@test.com")
    void t4_2() throws Exception {
        ResultActions resultActions = mvc.perform(
                delete("/api/messages")
                        .content("[3, 4]")
                        .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
        ).andDo(print());

        resultActions.andExpect(handler().handlerType(MessageController.class))
                .andExpect(handler().methodName("delete"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.result_code").value("403-2"))
                .andExpect(jsonPath("$.msg").value("작성자만 쪽지를 삭제할 수 있습니다."));
    }

    @Test
    @DisplayName("쪽지 확인(읽기)")
    @WithUserDetails("admin@test.com")
    void t5_1() throws Exception {
        List<Long> ids = List.of(1L, 3L);

        ResultActions resultActions = mvc.perform(
                put("/api/messages")
                        .content(new ObjectMapper().writeValueAsString(ids))
                        .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
        ).andDo(print());

        resultActions.andExpect(handler().handlerType(MessageController.class))
                .andExpect(handler().methodName("check"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result_code").value("200-3"))
                .andExpect(jsonPath("$.msg").value("%d개의 쪽지를 읽었습니다.".formatted(ids.size())));
    }

    @Test
    @DisplayName("내가 받지 않은 쪽지는 읽기 불가")
    @WithUserDetails("admin@test.com")
    void t5_2() throws Exception {
        ResultActions resultActions = mvc.perform(
                put("/api/messages")
                        .content("[1, 2]")
                        .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
        ).andDo(print());

        resultActions.andExpect(handler().handlerType(MessageController.class))
                .andExpect(handler().methodName("check"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.result_code").value("403-3"))
                .andExpect(jsonPath("$.msg").value("받는 사람만 쪽지를 읽을 수 있습니다."));
    }
}
