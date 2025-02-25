package com.NBE3_4_2_Team4.domain.board.message.controller;

import com.NBE3_4_2_Team4.domain.board.message.dto.MessageDto;
import com.NBE3_4_2_Team4.domain.board.message.dto.request.MessageWriteReqDto;
import com.NBE3_4_2_Team4.domain.board.message.service.MessageService;
import com.NBE3_4_2_Team4.global.rsData.RsData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
@Tag(name = "쪽지 관리", description = "쪽지 관련 API")
@RequiredArgsConstructor
public class MessageController {
    private final MessageService messageService;

    @GetMapping
    @Operation(summary = "쪽지 조회", description = "받은 쪽지 목록 조회")
    public List<MessageDto> getMessages() {
        return messageService.getMessages();
    }

    @GetMapping("/{id}")
    @Operation(summary = "쪽지 단건 조회", description = "쪽지 id에 해당하는 쪽지 조회")
    public MessageDto getMessage(@PathVariable long id) {
        return messageService.getMessage(id);
    }

    @PostMapping
    @Operation(summary = "쪽지 작성", description = "쪽지 작성")
    public RsData<MessageDto> write(@RequestBody MessageWriteReqDto reqBody) {
        return new RsData<>(
                "201-1",
                "쪽지를 성공적으로 보냈습니다.",
                messageService.write(reqBody.receiverName(), reqBody.content())
        );
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "쪽지 삭제", description = "쪽지 id에 해당하는 쪽지 삭제, 작성자만 삭제 가능")
    public RsData<Void> delete(@PathVariable long id) {
        messageService.delete(id);
        return new RsData<>(
                "200-1",
                "%d번 쪽지를 삭제하였습니다.".formatted(id)
        );
    }

    @PutMapping("/{id}")
    @Operation(summary = "쪽지 수정", description = "쪽지 id에 해당하는 쪽지 수정, 작성자만 수정 가능")
    public RsData<MessageDto> modify(@PathVariable long id, @RequestBody MessageWriteReqDto reqBody) {
        return new RsData<>(
                "200-2",
                "%d번 쪽지를 수정하였습니다.".formatted(id),
                messageService.modify(id, reqBody.content())
        );
    }

    @PutMapping("/{id}/read")
    @Operation(summary = "쪽지 읽음 표시", description = "쪽지 id에 해당하는 쪽지를 읽음 표시, 수신자만 읽기 가능")
    public RsData<Void> check(@PathVariable long id) {
        messageService.readMessage(id);
        return new RsData<>(
                "200-3",
                "%d번 쪽지를 읽었습니다.".formatted(id)
        );
    }
}
