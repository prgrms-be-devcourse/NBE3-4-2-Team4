package com.NBE3_4_2_Team4.domain.member.bankAccount.initData;

import com.NBE3_4_2_Team4.domain.member.bankAccount.dto.BankAccountRequestDto.GenerateBankAccount;
import com.NBE3_4_2_Team4.domain.member.bankAccount.service.BankAccountService;
import com.NBE3_4_2_Team4.domain.member.member.entity.Member;
import com.NBE3_4_2_Team4.domain.member.member.service.MemberService;
import com.NBE3_4_2_Team4.global.security.AuthManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class BankAccountInitData {

    @Autowired
    private BankAccountService bankAccountService;

    @Autowired
    private MemberService memberService;

    @Autowired
    @Lazy
    private BankAccountInitData self;

    @Bean
    public ApplicationRunner bankAccountInitDataApplicationRunner() {
        return _ -> {
            self.createInitBankAccounts();
        };
    }

    @Transactional
    public void createInitBankAccounts() {

        // 테스트 유저 로그인
        Optional<Member> opMember = memberService.signIn("test@test.com");

        if (opMember.isEmpty()) {
            return;
        }

        Member member = opMember.get();
        AuthManager authManager = new AuthManager();

        authManager.setLogin(member);

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