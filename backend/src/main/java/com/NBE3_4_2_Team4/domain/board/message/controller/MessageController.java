package com.NBE3_4_2_Team4.domain.board.message.controller;

import com.NBE3_4_2_Team4.domain.board.message.dto.MessageDto;
import com.NBE3_4_2_Team4.domain.board.message.dto.request.MessageWriteReqDto;
import com.NBE3_4_2_Team4.domain.board.message.service.MessageService;
import com.NBE3_4_2_Team4.global.rsData.RsData;
import com.NBE3_4_2_Team4.standard.dto.PageDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
@Tag(name = "쪽지 관리", description = "쪽지 관련 API")
@RequiredArgsConstructor
public class MessageController {
    private final MessageService messageService;

    @GetMapping("/send")
    @Operation(summary = "보낸 쪽지 조회", description = "보낸 쪽지 목록 조회")
    public PageDto<MessageDto> getSentMessages(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        return new PageDto<>(
                messageService.getSentMessages(page, pageSize)
        );
    }

    @GetMapping("/receive")
    @Operation(summary = "받은 쪽지 조회", description = "받은 쪽지 목록 조회")
    public PageDto<MessageDto> getReceivedMessages(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        return new PageDto<>(
                messageService.getReceivedMessages(page, pageSize)
        );
    }

    @GetMapping("/receive/unread")
    @Operation(summary = "읽지 않은 쪽지 조회", description = "받은 쪽지 중 읽지 않은 목록 조회")
    public Long getUnreadMessages() {
        return messageService.getUnreadMessages();
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
                messageService.write(reqBody.receiverName(), reqBody.title(), reqBody.content())
        );
    }

    @DeleteMapping
    @Operation(summary = "쪽지 삭제", description = "쪽지 id에 해당하는 쪽지 삭제, 작성자만 삭제 가능")
    public RsData<Void> delete(@RequestBody List<Long> ids) {
        messageService.delete(ids);
        return new RsData<>(
                "200-2",
                "%d개의 쪽지를 삭제하였습니다.".formatted(ids.size())
        );
    }

    @PutMapping
    @Operation(summary = "쪽지 읽음 표시", description = "쪽지 id에 해당하는 쪽지를 읽음 표시, 수신자만 읽기 가능")
    public RsData<Void> check(@RequestBody List<Long> ids) {
        messageService.readMessage(ids);
        return new RsData<>(
                "200-3",
                "%d개의 쪽지를 읽었습니다.".formatted(ids.size())
        );
    }
}
