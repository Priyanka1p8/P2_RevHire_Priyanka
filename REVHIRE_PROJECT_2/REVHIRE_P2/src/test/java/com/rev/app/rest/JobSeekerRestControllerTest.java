package com.rev.app.rest;

import com.rev.app.config.JwtUtil;
import com.rev.app.dto.JobSeekerDTO;
import com.rev.app.service.JobSeekerService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@RunWith(SpringRunner.class)
@WebMvcTest(JobSeekerRestController.class)
@AutoConfigureMockMvc(addFilters = false)
public class JobSeekerRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JobSeekerService jobSeekerService;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private UserDetailsService userDetailsService;

    @MockBean
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testGetProfile() throws Exception {
        Long userId = 1L;
        JobSeekerDTO dto = new JobSeekerDTO();
        dto.setName("John Doe");

        when(jobSeekerService.getProfileByUserId(userId)).thenReturn(dto);

        mockMvc.perform(get("/api/seeker/profile/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("John Doe"));
    }

    @Test
    public void testUpdateProfile() throws Exception {
        Long seekerId = 1L;
        JobSeekerDTO dto = new JobSeekerDTO();
        dto.setName("Updated Name");

        when(jobSeekerService.updateProfile(eq(seekerId), any(JobSeekerDTO.class))).thenReturn(dto);

        mockMvc.perform(put("/api/seeker/profile/{id}", seekerId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Name"));
    }

    @Test
    public void testGetDashboardData() throws Exception {
        Long seekerId = 1L;
        Map<String, Object> summary = new HashMap<>();
        summary.put("appliedCount", 10);

        when(jobSeekerService.getDashboardSummary(seekerId)).thenReturn(summary);

        mockMvc.perform(get("/api/seeker/{id}/dashboard", seekerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.appliedCount").value(10));
    }
}
