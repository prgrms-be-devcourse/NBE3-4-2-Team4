package com.NBE3_4_2_Team4.domain.board.message.initData;

import com.NBE3_4_2_Team4.domain.board.message.service.MessageService;
import com.NBE3_4_2_Team4.domain.member.member.initData.MemberInitData;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.transaction.annotation.Transactional;

@Configuration
@RequiredArgsConstructor
public class MessageInitData {
    private final MessageService messageService;
    private final MemberInitData memberInitData;

    @Value("${custom.initData.member.admin.username}")
    private String adminUsername;

    @Value("${custom.initData.member.member1.username}")
    private String member1Username;

    @Value("${custom.initData.member.member2.username}")
    private String member2Username;

    @Autowired
    @Lazy
    private MessageInitData self;

    @Bean
    public ApplicationRunner messageInitDataApplicationRunner() {
        return _ -> {
            memberInitData.work();
            self.work1();
        };
    }

    @Transactional
    public void work1() {
        if (messageService.count() > 0) return;

        messageService.write(member1Username, adminUsername, "문의드립니다.", "서비스 이용 중 궁금한 점이 있어 문의드립니다.");
        messageService.write(member1Username, member2Username, "오늘 일정 가능할까요?", "시간 괜찮으시면 연락 주세요.");
        messageService.write(member1Username, adminUsername, "결제 관련 문의", "결제 내역을 확인해 주실 수 있을까요?");

        messageService.write(adminUsername, member1Username, "문의 확인 완료", "문의 주신 사항을 검토하고 답변 드립니다.");
        messageService.write(adminUsername, member2Username, "공지사항 안내", "중요한 공지가 있어 전달드립니다.");
        messageService.write(adminUsername, member1Username, "업데이트 소식", "새로운 기능이 추가되었습니다!");

        messageService.write(member2Username, member1Username, "함께 공부할까요?", "스터디 모임에 관심 있으시면 알려주세요.");
        messageService.write(member2Username, adminUsername, "권한 요청", "추가 권한이 필요하여 요청드립니다.");
        messageService.write(member2Username, member1Username, "파일 공유", "필요한 자료를 보내드립니다.");
    }
}
