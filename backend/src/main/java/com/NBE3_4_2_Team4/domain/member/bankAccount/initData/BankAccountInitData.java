package com.NBE3_4_2_Team4.domain.member.bankAccount.initData;

import com.NBE3_4_2_Team4.domain.member.bankAccount.dto.BankAccountRequestDto.GenerateBankAccount;
import com.NBE3_4_2_Team4.domain.member.bankAccount.service.BankAccountService;
import com.NBE3_4_2_Team4.domain.member.member.entity.Member;
import com.NBE3_4_2_Team4.domain.member.member.initData.MemberInitData;
import com.NBE3_4_2_Team4.domain.member.member.repository.MemberRepository;
import com.NBE3_4_2_Team4.global.security.AuthManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

@Order(3)
@Slf4j
@Configuration
@RequiredArgsConstructor
public class BankAccountInitData {

    @Value("${custom.initData.member.member1.username}")
    private String member1Username;

    private final BankAccountService bankAccountService;
    private final MemberInitData memberInitData;
    private final MemberRepository memberRepository;

    @Autowired
    @Lazy
    private BankAccountInitData self;

    @Bean
    public ApplicationRunner bankAccountInitDataApplicationRunner() {
        return _ -> {
            memberInitData.work();
            self.createInitBankAccounts();
        };
    }

    @Transactional
    public void createInitBankAccounts() {

        // 테스트 유저 조회 + 로그인
        Member testUser = memberRepository.findByUsername(member1Username).orElseThrow();

        AuthManager authManager = new AuthManager();
        authManager.setLogin(testUser);

        // 기존에 생성된 계좌가 있는지 확인
        if (!bankAccountService.findAllBankAccount().isEmpty()) {
            return;
        }

        // 계좌 생성
        bankAccountService.generateBankAccount(
                GenerateBankAccount.builder()
                    .bankCode("031")
                    .accountNumber("16613084208")
                    .accountHolder("정성재")
                .build());

        SecurityContextHolder.clearContext();
    }
}