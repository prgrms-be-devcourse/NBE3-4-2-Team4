package com.NBE3_4_2_Team4.domain.board.message.controller

import com.NBE3_4_2_Team4.domain.board.message.dto.MessageDto
import com.NBE3_4_2_Team4.domain.board.message.dto.request.MessageWriteReqDto
import com.NBE3_4_2_Team4.domain.board.message.service.MessageService
import com.NBE3_4_2_Team4.global.rsData.RsData
import com.NBE3_4_2_Team4.global.security.AuthManager
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/messages")
@Tag(name = "쪽지 관리", description = "쪽지 관련 API")
class MessageController(
    private val messageService: MessageService
) {
    @GetMapping("/send")
    @Operation(summary = "보낸 쪽지 조회", description = "보낸 쪽지 목록 조회")
    fun getSentMessages(): List<MessageDto> = messageService.getSentMessages()

    @GetMapping("/receive")
    @Operation(summary = "받은 쪽지 조회", description = "받은 쪽지 목록 조회")
    fun getReceivedMessages(): List<MessageDto> = messageService.getReceivedMessages()

    @GetMapping("/receive/unread")
    @Operation(summary = "읽지 않은 쪽지 조회", description = "받은 쪽지 중 읽지 않은 목록 조회")
    fun getUnreadMessages(): Long = messageService.getUnreadMessages()

    @GetMapping("/{id}")
    @Operation(summary = "쪽지 단건 조회", description = "쪽지 id에 해당하는 쪽지 조회")
    fun getMessage(@PathVariable id: Long): MessageDto {
        return messageService.getMessage(id)
    }

    @PostMapping
    @Operation(summary = "쪽지 작성", description = "쪽지 작성")
    fun write(@RequestBody reqBody: MessageWriteReqDto): RsData<MessageDto> {
        return RsData(
                "201-1",
                "쪽지를 성공적으로 보냈습니다.",
                messageService.write(reqBody.receiverName, reqBody.title, reqBody.content)
        )
    }

    @DeleteMapping
    @Operation(summary = "쪽지 삭제", description = "쪽지 id에 해당하는 쪽지 삭제, 작성자만 삭제 가능")
    fun delete(@RequestBody ids: List<Long>): RsData<Void> {
        messageService.delete(ids)
        return RsData(
                "200-1",
                "${ids.size}개의 쪽지를 삭제하였습니다."
        )
    }

    @PutMapping
    @Operation(summary = "쪽지 읽음 표시", description = "쪽지 id에 해당하는 쪽지를 읽음 표시, 수신자만 읽기 가능")
    fun check(@RequestBody ids: List<Long>): RsData<Void> {
        messageService.readMessage(ids)
        return RsData(
                "200-2",
                "${ids.size}개의 쪽지를 읽었습니다."
        )
    }
}
