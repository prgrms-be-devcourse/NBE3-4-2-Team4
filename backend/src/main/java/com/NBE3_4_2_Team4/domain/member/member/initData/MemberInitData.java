package com.NBE3_4_2_Team4.domain.member.member.initData;

import com.NBE3_4_2_Team4.domain.asset.main.entity.AssetCategory;
import com.NBE3_4_2_Team4.domain.asset.main.entity.AssetHistory;
import com.NBE3_4_2_Team4.domain.asset.main.entity.AssetType;
import com.NBE3_4_2_Team4.domain.asset.main.repository.AssetHistoryRepository;
import com.NBE3_4_2_Team4.domain.member.member.entity.Member;
import com.NBE3_4_2_Team4.domain.member.member.entity.asset.Cash;
import com.NBE3_4_2_Team4.domain.member.member.entity.asset.Point;
import com.NBE3_4_2_Team4.domain.member.member.repository.MemberRepository;
import com.NBE3_4_2_Team4.standard.constants.PointConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

@Order(0)
@Slf4j
@Configuration
@RequiredArgsConstructor
public class MemberInitData {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final AssetHistoryRepository assetHistoryRepository;


    @Value("${custom.initData.member.admin.username}")
    private String adminUsername;
    @Value("${custom.initData.member.admin.password}")
    private String adminPassword;
    @Value("${custom.initData.member.admin.nickname}")
    private String adminNickname;
    @Value("${custom.initData.member.admin.email}")
    private String adminEmail;


    @Value("${custom.initData.member.member1.username}")
    private String member1Username;
    @Value("${custom.initData.member.member1.password}")
    private String member1Password;
    @Value("${custom.initData.member.member1.nickname}")
    private String member1Nickname;
    @Value("${custom.initData.member.member1.email}")
    private String member1Email;


    @Value("${custom.initData.member.member2.username}")
    private String member2Username;
    @Value("${custom.initData.member.member2.password}")
    private String member2Password;
    @Value("${custom.initData.member.member2.nickname}")
    private String member2Nickname;
    @Value("${custom.initData.member.member2.email}")
    private String member2Email;


    @Autowired
    @Lazy
    private MemberInitData self;

    @Bean
    public ApplicationRunner memberInitDataApplicationRunner() {
        return ignored -> {
            self.work();
        };
    }

    private void saveSignUpPoint(Member member) {
        assetHistoryRepository.save(
                new AssetHistory(
                        member,
                        PointConstants.INITIAL_POINT,
                        AssetType.POINT,
                        AssetCategory.SIGN_UP,
                        "d",
                        null,
                        null));

        member.setPoint(new Point(PointConstants.INITIAL_POINT));
    }

    private void saveSignUpCash(Member member) {
                assetHistoryRepository.save(
                        new AssetHistory(
                                member,
                                PointConstants.INITIAL_POINT,
                                AssetType.CASH,
                                AssetCategory.SIGN_UP,
                                "d",
                                null,
                                null));
        member.setCash(new Cash(1000L));
    }

    @Transactional
    public void work() {
        if (memberRepository.count() > 0) return;
        Member.OAuth2Provider oAuth2Provider = Member.OAuth2Provider.NONE;

        Member admin = memberRepository.save(Member.builder()
                .username(adminUsername)
                .password(passwordEncoder.encode(adminPassword))
                .nickname(adminNickname)
                .emailAddress(adminEmail)
                .emailVerified(true)
                .role(Member.Role.ADMIN)
                .oAuth2Provider(oAuth2Provider)
                .build());
        saveSignUpPoint(admin);
        saveSignUpCash(admin);

        Member member1 = memberRepository.save(Member.builder()
                .username(member1Username)
                .password(passwordEncoder.encode(member1Password))
                .nickname(member1Nickname)
                .emailAddress(member1Email)
                .emailVerified(true)
                .role(Member.Role.USER)
                .oAuth2Provider(oAuth2Provider)
                .build());
        saveSignUpPoint(member1);
        saveSignUpCash(member1);

        Member member2 = memberRepository.save(Member.builder()
                .username(member2Username)
                .password(passwordEncoder.encode(member2Password))
                .nickname(member2Nickname)
                .emailAddress(member2Email)
                .emailVerified(true)
                .role(Member.Role.USER)
                .oAuth2Provider(oAuth2Provider)
                .build());
        saveSignUpPoint(member2);
        saveSignUpCash(member2);
    }
}
