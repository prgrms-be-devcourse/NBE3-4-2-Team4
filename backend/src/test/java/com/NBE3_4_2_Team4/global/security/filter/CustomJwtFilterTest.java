package com.NBE3_4_2_Team4.global.security.filter;

import com.NBE3_4_2_Team4.domain.member.member.entity.Member;
import com.NBE3_4_2_Team4.global.security.jwt.JwtManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
public class CustomJwtFilterTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtManager jwtManager;

    private Member member;

    private Member admin;

    @BeforeEach
    void setUp() {
        member = Member.builder()
                .id(1L)
                .username("testUser")
                .nickname("nickname")
                .role(Member.Role.USER)
                .oAuth2Provider(Member.OAuth2Provider.NONE)
                .build();

        admin = Member.builder()
                .id(2L)
                .username("testAdmin")
                .nickname("nickname")
                .role(Member.Role.ADMIN)
                .oAuth2Provider(Member.OAuth2Provider.NONE)
                .build();
    }

    @Test
    public void testCustomJwtFilter1() throws Exception {
        mockMvc.perform(post("/api/test")
                        .with(csrf())
                )
                .andExpect(status().isOk());
    }

    @Test
    public void testCustomJwtFilter2() throws Exception {
        mockMvc.perform(post("/api/products/test")
                        .with(csrf())
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testCustomJwtFilter3() throws Exception {
        String jwtToken = jwtManager.generateToken(member);

        mockMvc.perform(post("/api/products/test")
                        .header("Authorization", String.format("Bearer %s", jwtToken))
                        .with(csrf())
                )
                .andExpect(status().isOk());
    }

    @Test
    public void testCustomJwtFilter4() throws Exception {
        String jwtToken = jwtManager.generateToken(member);

        mockMvc.perform(post("/api/questions/test")
                        .header("Authorization", String.format("Bearer %s", jwtToken))
                        .with(csrf())
                )
                .andExpect(status().isOk());
    }

    @Test
    public void testCustomJwtFilter5() throws Exception {
        String jwtToken = jwtManager.generateToken(member);

        mockMvc.perform(post("/api/answers/test")
                        .header("Authorization", String.format("Bearer %s", jwtToken))
                        .with(csrf())
                )
                .andExpect(status().isOk());
    }

    @Test
    public void testCustomJwtFilter6() throws Exception {
        String jwtToken = jwtManager.generateToken(member);

        mockMvc.perform(post("/api/admin/test")
                        .header("Authorization", String.format("Bearer %s", jwtToken))
                        .with(csrf())
                )
                .andExpect(status().isForbidden());
    }

    @Test
    public void testCustomJwtFilter7() throws Exception {
        String jwtToken = jwtManager.generateToken(admin);

        mockMvc.perform(post("/api/admin/test")
                        .header("Authorization", String.format("Bearer %s", jwtToken))
                        .with(csrf())
                )
                .andExpect(status().isOk());
    }
}
