package com.NBE3_4_2_Team4.domain.member.member.service;

import com.NBE3_4_2_Team4.domain.asset.main.entity.AssetCategory;
import com.NBE3_4_2_Team4.domain.asset.main.entity.AssetType;
import com.NBE3_4_2_Team4.domain.member.OAuth2RefreshToken.entity.OAuth2RefreshToken;
import com.NBE3_4_2_Team4.domain.member.OAuth2RefreshToken.repository.OAuth2RefreshTokenRepository;
import com.NBE3_4_2_Team4.domain.member.member.entity.asset.Point;
import com.NBE3_4_2_Team4.domain.member.member.dto.AdminLoginRequestDto;
import com.NBE3_4_2_Team4.domain.member.member.dto.MemberDetailInfoResponseDto;
import com.NBE3_4_2_Team4.domain.member.member.dto.NicknameUpdateRequestDto;
import com.NBE3_4_2_Team4.domain.member.member.dto.SignupRequestDto;
import com.NBE3_4_2_Team4.domain.member.member.entity.Member;
import com.NBE3_4_2_Team4.domain.member.member.repository.MemberQuerydsl;
import com.NBE3_4_2_Team4.domain.member.member.repository.MemberRepository;
import com.NBE3_4_2_Team4.domain.asset.main.entity.AssetHistory;
import com.NBE3_4_2_Team4.domain.asset.main.repository.AssetHistoryRepository;
import com.NBE3_4_2_Team4.global.exceptions.InValidPasswordException;
import com.NBE3_4_2_Team4.global.exceptions.MemberNotFoundException;
import com.NBE3_4_2_Team4.global.exceptions.ServiceException;
import com.NBE3_4_2_Team4.global.security.jwt.JwtManager;
import com.NBE3_4_2_Team4.global.security.oauth2.OAuth2Manager;
import com.NBE3_4_2_Team4.global.security.oauth2.disconectService.OAuth2DisconnectService;
import com.NBE3_4_2_Team4.global.security.oauth2.logoutService.OAuth2LogoutService;
import com.NBE3_4_2_Team4.global.security.user.TempUserBeforeSignUp;
import com.NBE3_4_2_Team4.standard.constants.PointConstants;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;

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


    private final JwtManager jwtManager;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;


    @Transactional(readOnly = true)
    public long count(){
        return memberRepository.count();
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



    public boolean duplicateNickname(String nickname) {
        return !memberRepository.existsByUsername(nickname);
    }


    public String getLogoutUrl(Member member){
        Member.OAuth2Provider oAuthProvider = member.getOAuth2Provider();

        OAuth2LogoutService oAuth2LogoutService = oAuth2Manager.getOAuth2LogoutService(oAuthProvider);
        if (oAuth2LogoutService == null) {
            throw new RuntimeException("Logout service not found");
        }
        return oAuth2LogoutService.getLogoutUrl();
    }




    public Member signUp(
            String username,
            String password,
            String nickname,
            Member.Role role,
            Member.OAuth2Provider oAuth2Provider){
        if (memberRepository.existsByUsername(username)) {
            throw new ServiceException("400-1", String.format("member already exist with name %s", username));
        }
        Member member = memberRepository.save(Member.builder()
                .role(role)
                .oAuth2Provider(oAuth2Provider)
                .username(username)
                .password(passwordEncoder.encode(password))
                .nickname(nickname)
                .build());
        saveInitialPoints(member);
        return member;
    }


    public Member signUp(String tempToken, SignupRequestDto signupRequestDto){
        Map<String, Object> claims = jwtManager.getClaims(tempToken);
        String oAuth2Id = (String) claims.get("oAuth2Id");

        TempUserBeforeSignUp tempUserBeforeSignUp =
                objectMapper.convertValue(redisTemplate.opsForValue().get(oAuth2Id), TempUserBeforeSignUp.class);


        String username = tempUserBeforeSignUp.getUsername();
        String realName = tempUserBeforeSignUp.getRealName();
        String provider = tempUserBeforeSignUp.getProviderTypeCode();
        String nickname = signupRequestDto.nickname();
        String emailAddress = signupRequestDto.email();

        Member member = memberRepository.save(Member.builder()
                        .role(Member.Role.USER)
                .oAuth2Provider(Member.OAuth2Provider.getOAuth2ProviderByName(provider))
                .username(username)
                .password(passwordEncoder.encode(""))
                .nickname(nickname)
                .emailAddress(emailAddress)
                .realName(realName)
                .build());
        saveInitialPoints(member);

        redisTemplate.delete(oAuth2Id);

        return member;
    }


    private void saveInitialPoints(Member member) {
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




    public Member userSignUp(
            String username,
            String password,
            String nickname,
            Member.OAuth2Provider oAuth2Provider){
        return signUp(username, password, nickname, Member.Role.USER, oAuth2Provider);
    }




    public MemberDetailInfoResponseDto getMemberDetailInfo(Member member){
        return memberQuerydsl.getMemberDetailInfo(member);
    }




    public void updateNickname(Member member, NicknameUpdateRequestDto nicknameUpdateRequestDto){
        Member memberData = memberRepository.findById(member.getId())
                .orElseThrow(() -> new ServiceException("404-1", String.format("no member found with id %d", member.getId())));
        String newNickname = nicknameUpdateRequestDto.newNickname();
        memberData.setNickname(newNickname);
    }


    public Optional<Member> signIn(String username){
        return memberRepository.findByUsername(username);
    }


    public Member signUpOrIn(String username, String password, String nickname, Member.OAuth2Provider oAuth2Provider) {
        Optional<Member> member = memberRepository.findByUsername(username);
        return member.orElseGet(() -> userSignUp(username, password, nickname, oAuth2Provider));
    }




    public void withdrawalMembership(Member member) {
        Long memberId = member.getId();

        if (!memberRepository.existsById(memberId)) {
            throw new ServiceException("404-1", String.format("no member found with id %d", memberId));
        }

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
