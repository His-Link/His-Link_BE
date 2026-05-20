package com.hislink.domain.main.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class MainDashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getDashboard_isPublicAndReturnsPayload() throws Exception {
        mockMvc.perform(get("/api/main/dashboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.latestCommunityPosts").isArray())
                .andExpect(jsonPath("$.data.latestProjects").isArray())
                .andExpect(jsonPath("$.data.latestRecruitmentPosts").isArray())
                .andExpect(jsonPath("$.data.popularProjects").isArray())
                .andExpect(jsonPath("$.data.topFeedbackProjects").isArray());
    }
}
