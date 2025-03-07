//package com.NBE3_4_2_Team4.domain.member.OAuth2RefreshToken.service;
//
//import com.NBE3_4_2_Team4.domain.member.OAuth2RefreshToken.entity.OAuth2RefreshToken;
//import com.NBE3_4_2_Team4.domain.member.OAuth2RefreshToken.repository.OAuth2RefreshTokenRepository;
//import com.NBE3_4_2_Team4.domain.member.member.entity.Member;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//@Service
//@RequiredArgsConstructor
//public class OAuth2RefreshTokenService {
//    private final OAuth2RefreshTokenRepository oAuth2RefreshTokenRepository;
//
//    public void saveOrUpdateOAuth2RefreshToken(Member member, String refreshToken, String oAuth2Id){
//        if (refreshToken != null && !refreshToken.isBlank()) {
//            OAuth2RefreshToken oAuth2RefreshToken = oAuth2RefreshTokenRepository.findByMember(member)
//                    .orElse(null); // 먼저 찾기만 함
//            if (oAuth2RefreshToken != null) {
//                // 이미 존재하는 경우 업데이트
//                oAuth2RefreshToken.setRefreshToken(refreshToken);
//                oAuth2RefreshTokenRepository.save(oAuth2RefreshToken); // 업데이트
//            } else {
//                // 없으면 새로 저장
//                oAuth2RefreshTokenRepository.save(OAuth2RefreshToken.builder()
//                        .member(member)
//                        .oAuth2Id(oAuth2Id)
//                        .refreshToken(refreshToken)
//                        .build());
//            }
//        }
//    }
//}
