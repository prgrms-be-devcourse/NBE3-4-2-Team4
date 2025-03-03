package com.NBE3_4_2_Team4.domain.member.bankAccount.entity;

import com.NBE3_4_2_Team4.domain.member.member.entity.Member;
import com.NBE3_4_2_Team4.global.exceptions.ServiceException;
import com.NBE3_4_2_Team4.global.jpa.entity.BaseTime;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(force = true)
@AllArgsConstructor
@Builder
public class BankAccount extends BaseTime {

    @Column(nullable = false)
    private final String bankCode;          // 은행 코드

    @Column(nullable = false)
    private final String bankName;          // 은행 이름

    @Column(nullable = false)
    private final String accountNumber;     // 계좌 번호

    @Column(nullable = false)
    private String maskedAccountNumber;     // 마스킹 된 계좌 번호

    @Column(nullable = false)
    private final String accountHolder;     // 예금주

    private String nickname;                // 계좌 별명

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;                  // 회원 정보

    public void updateNickname(String newNickname) {

        if (newNickname == null || newNickname.isEmpty()) {
            if (this.accountNumber == null || this.bankName == null) {
                throw new ServiceException("404-1", "계좌 번호 또는 은행 이름이 존재하지 않아 기본 계좌 별칭을 설정할 수 없습니다.");

            }

            this.nickname = "%s %s"
                    .formatted(
                            this.bankName,
                            this.accountNumber.substring(this.accountNumber.length() - 5)
                    );

        } else {
            this.nickname = newNickname;

        }
    }
}