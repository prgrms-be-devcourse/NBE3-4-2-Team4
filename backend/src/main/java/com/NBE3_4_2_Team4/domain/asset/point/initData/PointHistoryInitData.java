package com.NBE3_4_2_Team4.domain.asset.point.initData;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

@Order(5)
@Configuration
@RequiredArgsConstructor
public class PointHistoryInitData {
//    private final PointHistoryService pointHistoryService;
//    private final MemberRepository memberRepository;
//    @Autowired
//    @Lazy
//    private PointHistoryInitData self;
//
//    @Value("${custom.initData.member.member1.username}")
//    private String member1Username;
//
//    @Value("${custom.initData.member.admin.username}")
//    private String adminUsername;
//
//    @Bean
//    @DependsOn("memberInitDataApplicationRunner")
//    public ApplicationRunner pointInitDataApplicationRunner() {
//        return args -> {
//            self.work();
//        };
//    }
//
//    @EventListener(ApplicationReadyEvent.class)
//    @Transactional
//    public void work() {
//        if (pointHistoryService.count() > 0) return;
//
//        Member admin = memberRepository.findByUsername(adminUsername).get();
//        Member member = memberRepository.findByUsername(member1Username).get();
//
//
//        pointHistoryService.createHistory(admin, null, 10, PointCategory.ANSWER, "");
//        pointHistoryService.createHistory(admin, null, 10, PointCategory.ANSWER, "");
//        pointHistoryService.createHistory(admin, member, 13, PointCategory.TRANSFER, "");
//        pointHistoryService.createHistory(admin, null, 14, PointCategory.PURCHASE, "");
//        pointHistoryService.createHistory(admin, null, 15, PointCategory.ATTENDANCE, "");
//    }
}
