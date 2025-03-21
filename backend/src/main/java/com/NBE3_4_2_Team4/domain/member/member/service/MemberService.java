package com.NBE3_4_2_Team4.domain.member.member.service;

import com.NBE3_4_2_Team4.domain.asset.main.entity.AssetCategory;
import com.NBE3_4_2_Team4.domain.asset.main.entity.AssetType;
import com.NBE3_4_2_Team4.domain.member.OAuth2RefreshToken.entity.OAuth2RefreshToken;
import com.NBE3_4_2_Team4.domain.member.OAuth2RefreshToken.repository.OAuth2RefreshTokenRepository;
import com.NBE3_4_2_Team4.domain.member.member.dto.*;
import com.NBE3_4_2_Team4.domain.member.member.entity.asset.Point;
import com.NBE3_4_2_Team4.domain.member.member.entity.Member;
import com.NBE3_4_2_Team4.domain.member.member.repository.MemberQuerydsl;
import com.NBE3_4_2_Team4.domain.member.member.repository.MemberRepository;
import com.NBE3_4_2_Team4.domain.asset.main.entity.AssetHistory;
import com.NBE3_4_2_Team4.domain.asset.main.repository.AssetHistoryRepository;
import com.NBE3_4_2_Team4.global.exceptions.EmailAlreadyVerifiedException;
import com.NBE3_4_2_Team4.global.exceptions.InValidPasswordException;
import com.NBE3_4_2_Team4.global.exceptions.MemberNotFoundException;
import com.NBE3_4_2_Team4.global.exceptions.ServiceException;
import com.NBE3_4_2_Team4.global.mail.service.MailService;
import com.NBE3_4_2_Team4.global.security.oauth2.OAuth2Manager;
import com.NBE3_4_2_Team4.global.security.oauth2.disconectService.OAuth2DisconnectService;
import com.NBE3_4_2_Team4.global.security.oauth2.logoutService.OAuth2LogoutService;
import com.NBE3_4_2_Team4.global.security.user.tempUserBeforeSignUp.TempUserBeforeSignUp;
import com.NBE3_4_2_Team4.global.security.user.tempUserBeforeSignUp.TempUserBeforeSignUpService;
import com.NBE3_4_2_Team4.standard.constants.PointConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final MemberQuerydsl memberQuerydsl;
    private final AssetHistoryRepository assetHistoryRepository;

    private final PasswordEncoder passwordEncoder;
    private final OAuth2Manager oAuth2Manager;
    private final OAuth2RefreshTokenRepository oAuth2RefreshTokenRepository;

    private final TempUserBeforeSignUpService tempUserBeforeSignUpService;
    private final MailService mailService;


    private void checkIfMemberExists(Long memberId){
        if(!memberRepository.existsById(memberId)){
            throw new ServiceException("404-1", String.format("no member found with id %d", memberId));
        }
    }

    public Member adminLogin(AdminLoginRequestDto adminLoginRequestDto) {
        String adminUsername = adminLoginRequestDto.adminUsername();
        Member member = memberRepository.findByUsername(adminUsername)
                .orElseThrow(() -> new MemberNotFoundException(adminUsername));

        if (!passwordEncoder.matches(adminLoginRequestDto.password(), member.getPassword())) {
            throw new InValidPasswordException();
        }
        if (!member.getRole().equals(Member.Role.ADMIN)){
            throw new RuntimeException("Role not allowed");
        }
        return member;
    }



    public boolean isNicknameAvailable(String nickname) {
        return !memberRepository.existsByUsername(nickname);
    }


    public String getLogoutUrl(Member member){
        checkIfMemberExists(member.getId());

        Member.OAuth2Provider oAuthProvider = member.getOAuth2Provider();

        OAuth2LogoutService oAuth2LogoutService = oAuth2Manager.getOAuth2LogoutService(oAuthProvider);
        if (oAuth2LogoutService == null) {
            throw new RuntimeException("Logout service not found");
        }
        return oAuth2LogoutService.getLogoutUrl();
    }



    public void signUp(String tempToken, SignupRequestDto signupRequestDto){
        TempUserBeforeSignUp tempUserBeforeSignUp =
                tempUserBeforeSignUpService.getTempUserFromRedisWithJwt(tempToken);

        Member member = saveMember(tempUserBeforeSignUp, signupRequestDto);

        saveOAuth2RefreshToken(member, tempUserBeforeSignUp);

        saveSignupPoints(member);

        tempUserBeforeSignUpService.deleteTempUserFromRedis(tempToken);

        Long memberId = member.getId();
        String emailAddress = member.getEmailAddress();

        sendAuthenticationMail(memberId, emailAddress);
    }


    public void sendAuthenticationMail(Long memberId, String emailAddress){
        checkIfMemberExists(memberId);

        String authCode = UUID.randomUUID().toString();

        tempUserBeforeSignUpService.saveAuthCodeForMember(memberId, authCode);

        mailService.sendAuthenticationMail(emailAddress, memberId, authCode);
    }


    private Member saveMember(TempUserBeforeSignUp tempUser, SignupRequestDto signupRequestDto) {
        if (memberRepository.existsByUsername(tempUser.getUsername())) {
            throw new ServiceException("409-1", String.format("already exists with username %s", tempUser.getUsername()));
        }
        return memberRepository.saveAndFlush(Member.builder()
                .role(Member.Role.USER)
                .oAuth2Provider(Member.OAuth2Provider.getOAuth2ProviderByName(tempUser.getProviderTypeCode()))
                .username(tempUser.getUsername())
                .password(passwordEncoder.encode(""))
                .nickname(signupRequestDto.nickname())
                .emailAddress(signupRequestDto.email())
                .realName(tempUser.getRealName())
                .build());
    }


    private void saveOAuth2RefreshToken(Member member, TempUserBeforeSignUp tempUser) {
        oAuth2RefreshTokenRepository.save(OAuth2RefreshToken.builder()
                .oAuth2Id(tempUser.getOAuth2Id())
                .member(member)
                .refreshToken(tempUser.getRefreshToken())
                .build());
    }


    private void saveSignupPoints(Member member) {
        try {
            assetHistoryRepository.save(AssetHistory.builder()
                    .member(member)
                    .amount(PointConstants.INITIAL_POINT)
                    .assetCategory(AssetCategory.SIGN_UP)
                    .assetType(AssetType.POINT)
                    .correlationId("asdsaaddasasddsa")
                    .build());
            member.setPoint(new Point(PointConstants.INITIAL_POINT));
        } catch (Exception e) {
            log.error("포인트 저장 실패: {}", e.getMessage());
        }
    }


    public boolean verifyEmail(Long memberId, String authCode) {
        checkIfMemberExists(memberId);

        Member member = memberRepository.findById(memberId).orElseThrow();

        if (member.isEmailVerified()){
            throw new EmailAlreadyVerifiedException();
        }

        boolean isEmailVerified = tempUserBeforeSignUpService
                .isEmailVerified(memberId, authCode);

        member.setEmailVerified(isEmailVerified);

        return isEmailVerified;
    }


    public MemberDetailInfoResponseDto getMemberDetailInfo(Member member){
        checkIfMemberExists(member.getId());
        return memberQuerydsl.getMemberDetailInfo(member);
    }


    public void updateNickname(Member member, NicknameUpdateRequestDto nicknameUpdateRequestDto){
        checkIfMemberExists(member.getId());

        Member memberData = memberRepository.findById(member.getId())
                .orElseThrow();

        String newNickname = nicknameUpdateRequestDto.newNickname();
        memberData.setNickname(newNickname);
    }


    public Optional<Member> findByUsername(String username){
        return memberRepository.findByUsername(username);
    }


    public void withdrawalMembership(Member member) {
        Long memberId = member.getId();

        checkIfMemberExists(memberId);

        Member.OAuth2Provider oAuth2Provider = member.getOAuth2Provider();

        if (!oAuth2Provider.equals(Member.OAuth2Provider.NONE)) {
            OAuth2RefreshToken oAuth2RefreshToken = oAuth2RefreshTokenRepository
                    .findByMember(member)
                    .orElseThrow();
            String refreshToken = oAuth2RefreshToken.getRefreshToken();

            OAuth2DisconnectService oAuth2DisconnectService = oAuth2Manager.getOAuth2DisconnectService(oAuth2Provider);

            if (oAuth2DisconnectService == null) {
                log.error("연동 서비스에 해당하는 DisconnectService 클래스를 찾을 수 없습니다. OAuth2Manager  OAuth2Provider : {}OAuth2Id : {}", oAuth2Provider, oAuth2RefreshToken.getOAuth2Id());
            }else if(!oAuth2DisconnectService.disconnectSuccess(refreshToken)){
                log.error("OAuth2 연동 해제 실패. (연동 해제 요청이 실패했습니다.) 해당 서비스에 직접 연결 해제를 시도하세요. OAuth2Provider : {}, OAuth2Id : {}", oAuth2Provider, oAuth2RefreshToken.getOAuth2Id());
            }
        }
        memberQuerydsl.deleteMember(memberId);
    }
}
