//package com.NBE3_4_2_Team4.global.security.jwt;
//
//import com.NBE3_4_2_Team4.domain.member.member.entity.Member;
//import com.NBE3_4_2_Team4.standard.constants.AuthConstants;
//import org.springframework.stereotype.Component;
//
//import java.util.Map;
//
//@Component
//public class JwtObjectMapper {
//    public Member getMemberByJwtClaims(Map<String, Object> claims) {
//        Integer id = (Integer) claims.get(AuthConstants.ID);
//        String username = (String) claims.get(AuthConstants.USERNAME);
//        String nickname = (String) claims.get(AuthConstants.NICKNAME);
//        String roleName = (String) claims.get(AuthConstants.ROLE);
//        String OAuth2ProviderName = (String) claims.get(AuthConstants.OAUTH2_PROVIDER);
//        String emailAddress = (String) claims.get(AuthConstants.EMAIL_ADDRESS);
//        Boolean emailVerified = (Boolean) claims.get(AuthConstants.EMAIL_VERIFIED);
//
//        if (id == null
//                || isNullOrBlank(username)
//                || isNullOrBlank(nickname)
//                || isNullOrBlank(roleName)
//                || isNullOrBlank(OAuth2ProviderName)
//                || isNullOrBlank(emailAddress)
//                || emailVerified == null) {
//            throw new RuntimeException("Invalid claims");
//        }
//
//        return new Member(Long.valueOf(id), username, nickname, roleName, OAuth2ProviderName, emailAddress, emailVerified);
//    }
//
//    private boolean isNullOrBlank(String string) {
//        return string == null || string.isBlank();
//    }
//}
