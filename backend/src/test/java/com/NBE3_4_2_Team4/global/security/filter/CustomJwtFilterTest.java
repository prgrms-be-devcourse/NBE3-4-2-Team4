package com.NBE3_4_2_Team4.global.security.filter;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
public class CustomJwtFilterTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testCustomJwtFilter1() throws Exception {
        mockMvc.perform(post("/api/test"))
                .andExpect(status().isOk());
    }

    @Test
    public void testCustomJwtFilter2() throws Exception {
        mockMvc.perform(post("/api/products/test"))
                .andExpect(status().isForbidden());
    }
}
