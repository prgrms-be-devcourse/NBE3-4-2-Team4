package com.NBE3_4_2_Team4.global.api.iamport.v1;

import java.util.List;
import java.util.Optional;

import static com.NBE3_4_2_Team4.global.api.iamport.v1.account.IamportAccountRequestDto.*;
import static com.NBE3_4_2_Team4.global.api.iamport.v1.account.IamportAccountResponseDto.*;

public interface IamportService {

    /**
     * @apiNote 아임포트 V1 인증 토큰 요청 + Redis 저장
     * @implNote
     * 토큰 사용법 : 다른 API 호출 시 Authorization Header에 Bearer <액세스 토큰> 형식으로 사용
     * 토큰 만료 시간 : 발행시간으로부터 30분
     * 토큰 재갱신 시간 : 만료 시간으로부터 1분 이내로 남았을 경우, 5분 재갱신
     */
    Optional<String> generateAccessToken(Long memberId);

    /**
     * @apiNote 아임포트 V1 인증 토큰 조회 (Redis)
     */
    Optional<String> getAccessToken(Long memberId);

    /**
     * @apiNote 아임포트 V1 예금주 조회
     */
    Optional<String> validateBankAccount(String accessToken, BankAccountValidator bankAccount);

    /**
     * @apiNote 아임포트 V1 은행 코드 전체 검색
     */
    List<BankInfo> getBankCodes(String accessToken);

    /**
     * @apiNote 아임포트 V1 은행명 조회
     */
    Optional<BankInfo> findBankNameByBankCode(String accessToken, String bankCode);
}