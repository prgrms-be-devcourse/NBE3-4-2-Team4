//package com.NBE3_4_2_Team4.global.security.jwt;
//
//import com.NBE3_4_2_Team4.domain.member.member.entity.Member;
//import com.NBE3_4_2_Team4.standard.constants.AuthConstants;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.params.ParameterizedTest;
//import org.junit.jupiter.params.provider.MethodSource;
//import org.springframework.test.context.ActiveProfiles;
//
//import java.util.HashMap;
//import java.util.Map;
//import java.util.stream.Stream;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@ActiveProfiles("test")
//public class JwtObjectMapperTest {
//    private JwtObjectMapper jwtObjectMapper;
//
//    private Map<String, Object> claims;
//
//    @BeforeEach
//    void setUp() {
//        jwtObjectMapper = new JwtObjectMapper();
//        claims = new HashMap<>();
//        claims.put(AuthConstants.ID, 1);
//        claims.put(AuthConstants.USERNAME, "testUser");
//        claims.put(AuthConstants.NICKNAME, "testNick");
//        claims.put(AuthConstants.ROLE, "USER");
//        claims.put(AuthConstants.OAUTH2_PROVIDER, "NONE");
//        claims.put(AuthConstants.EMAIL_ADDRESS, "testEmail");
//        claims.put(AuthConstants.EMAIL_VERIFIED, true);
//    }
//
//    @AfterEach
//    void tearDown() {
//        claims.clear();
//    }
//
//    static Stream<String> provideInvalidClaims() {
//        return Stream.of(AuthConstants.USERNAME, AuthConstants.NICKNAME, AuthConstants.ROLE, AuthConstants.OAUTH2_PROVIDER);
//    }
//
//    @Test
//    void getMemberByJwtClaimsTest(){
//        Member member = jwtObjectMapper.getMemberByJwtClaims(claims);
//
//        assertNotNull(member);
//        assertEquals(1L, member.getId());
//        assertEquals("testUser", member.getUsername());
//        assertEquals("testNick", member.getNickname());
//        assertEquals(Member.Role.USER, member.getRole());
//        assertEquals(Member.OAuth2Provider.NONE, member.getOAuth2Provider());
//        assertTrue(member.isEmailVerified());
//    }
//
//    @Test
//    void getMemberByJwtClaims_NullId() {
//        claims.remove(AuthConstants.ID);
//
//        Exception exception = assertThrows(RuntimeException.class, () -> jwtObjectMapper.getMemberByJwtClaims(claims));
//        assertEquals("Invalid claims", exception.getMessage());
//    }
//
//    @ParameterizedTest
//    @MethodSource("provideInvalidClaims")
//    void getMemberByJwtClaims_InvalidClaims_Null(String invalidKey) {
//        claims.remove(invalidKey);
//
//        Exception exception = assertThrows(RuntimeException.class, () -> jwtObjectMapper.getMemberByJwtClaims(claims));
//        assertEquals("Invalid claims", exception.getMessage());
//    }
//
//    @ParameterizedTest
//    @MethodSource("provideInvalidClaims")
//    void getMemberByJwtClaims_InvalidClaims_Blank(String invalidKey) {
//        claims.put(invalidKey, "");
//
//        Exception exception = assertThrows(RuntimeException.class, () -> jwtObjectMapper.getMemberByJwtClaims(claims));
//        assertEquals("Invalid claims", exception.getMessage());
//    }
//}
