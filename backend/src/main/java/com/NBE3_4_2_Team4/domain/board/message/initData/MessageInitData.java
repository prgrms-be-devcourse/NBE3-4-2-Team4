package com.NBE3_4_2_Team4.domain.board.message.initData;

import com.NBE3_4_2_Team4.domain.board.message.service.MessageService;
import com.NBE3_4_2_Team4.domain.member.member.entity.Member;
import com.NBE3_4_2_Team4.domain.member.member.initData.MemberInitData;
import com.NBE3_4_2_Team4.domain.member.member.repository.MemberRepository;
import com.NBE3_4_2_Team4.global.security.AuthManager;
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
    private final MemberRepository memberRepository;

    @Value("${custom.initData.member.admin.username}")
    private String adminUsername;

    @Value("${custom.initData.member.member1.username}")
    private String member1Username;

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

        AuthManager authManager = new AuthManager();

        Member testUser = memberRepository.findByUsername(member1Username).orElseThrow();
        authManager.setLogin(testUser);

        // 받은 쪽지(관리자)
        messageService.write("관리자", "문의드립니다.", "서비스 이용 중 궁금한 점이 있어 문의드립니다.");
        messageService.write("관리자", "결제 관련 문의", "결제 내역을 확인해 주실 수 있을까요?");
        messageService.write("관리자", "권한 요청", "추가 권한이 필요하여 요청드립니다.");

        messageService.write("관리자", "문의드립니다.2", "서비스 이용 중 궁금한 점이 있어 문의드립니다.");
        messageService.write("관리자", "결제 관련 문의2", "결제 내역을 확인해 주실 수 있을까요?");
        messageService.write("관리자", "권한 요청2", "추가 권한이 필요하여 요청드립니다.");
        messageService.write("관리자", "문의드립니다.3", "서비스 이용 중 궁금한 점이 있어 문의드립니다.");
        messageService.write("관리자", "결제 관련 문의3", "결제 내역을 확인해 주실 수 있을까요?");
        messageService.write("관리자", "권한 요청3", "추가 권한이 필요하여 요청드립니다.");
        messageService.write("관리자", "문의드립니다.4", "서비스 이용 중 궁금한 점이 있어 문의드립니다.");
        messageService.write("관리자", "결제 관련 문의4", "결제 내역을 확인해 주실 수 있을까요?");
        messageService.write("관리자", "권한 요청4", "추가 권한이 필요하여 요청드립니다.");

        Member admin = memberRepository.findByUsername(adminUsername).orElseThrow();
        authManager.setLogin(admin);

        // 보낸 쪽지(관리자)
        messageService.write("테스트 유저", "문의 확인 완료", "문의 주신 사항을 검토하고 답변 드립니다.");
        messageService.write("테스트 유저", "업데이트 소식", "새로운 기능이 추가되었습니다!");
        messageService.write("테스트 유저 2", "공지사항 안내", "중요한 공지가 있어 전달드립니다.");
    }
}
