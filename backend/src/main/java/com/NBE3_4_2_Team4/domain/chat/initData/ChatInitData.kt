package com.NBE3_4_2_Team4.domain.chat.initData

import com.NBE3_4_2_Team4.domain.chat.chat.service.ChatService
import com.NBE3_4_2_Team4.domain.chat.chatRoom.service.ChatRoomService
import com.NBE3_4_2_Team4.domain.member.member.initData.MemberInitData
import com.NBE3_4_2_Team4.domain.member.member.service.MemberService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import org.springframework.transaction.annotation.Transactional

@Configuration
class ChatInitData(
    private val memberService: MemberService,
    private val memberInitData: MemberInitData,
    private val chatRoomService: ChatRoomService,
    private val chatService: ChatService
) {
    @Value("\${custom.initData.member.admin.username}")
    lateinit var adminUsername: String

    @Value("\${custom.initData.member.member1.username}")
    lateinit var member1Username: String

    @Autowired
    @Lazy
    private lateinit var self: ChatInitData

    @Bean
    fun chatInitDataApplicationRunner(): ApplicationRunner {
        return ApplicationRunner {
            memberInitData.work()
            self.initChatData()
        }
    }

    @Transactional
    fun initChatData() {
        if (chatRoomService.count() > 0) return

        val member1 = memberService.findByUsername(adminUsername)!!
        val member2 = memberService.findByUsername(member1Username)!!

        val chatRoom = chatRoomService.save(member1, member2, "채팅방 1")

        chatService.save(chatRoom, member1, "안녕하세요")
        chatService.save(chatRoom, member2, "네~ 안녕하세요")
        chatService.save(chatRoom, member1, "저 궁금한게 있어서 문의 드리는데요")
        chatService.save(chatRoom, member2, "무엇이 궁금 하신가요?")
        chatService.save(chatRoom, member1, "혹시 오늘 점심 때 어떤 걸 드셨나요?")
        chatService.save(chatRoom, member2, "저 오늘 부대찌개 먹었습니다")
        chatService.save(chatRoom, member1, "감사합니다")
    }
}