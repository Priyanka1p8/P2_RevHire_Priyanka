package com.rev.app.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rev.app.config.JwtUtil;
import com.rev.app.dto.ApplicationDTO;
import com.rev.app.entity.Application.ApplicationStatus;
import com.rev.app.service.ApplicationService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(ApplicationRestController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ApplicationRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ApplicationService applicationService;

    @MockBean
    private UserDetailsService userDetailsService;

    @MockBean
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testApply() throws Exception {
        ApplicationDTO dto = new ApplicationDTO();
        dto.setJobId(1L);

        when(applicationService.applyToJob(any())).thenReturn(dto);

        mockMvc.perform(post("/api/applications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jobId").value(1));
    }

    @Test
    public void testGetBySeeker() throws Exception {
        when(applicationService.getApplicationsBySeeker(1L)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/applications/seeker/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    public void testUpdateStatus() throws Exception {
        ApplicationDTO dto = new ApplicationDTO();
        dto.setId(1L);
        dto.setStatus(ApplicationStatus.SHORTLISTED);

        when(applicationService.updateApplicationStatus(1L, "SHORTLISTED", "test")).thenReturn(dto);

        mockMvc.perform(put("/api/applications/1/status")
                .param("status", "SHORTLISTED")
                .param("comment", "test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SHORTLISTED"));
    }

    @Test
    public void testGetByJob() throws Exception {
        when(applicationService.getApplicationsByJob(1L)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/applications/job/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    public void testWithdraw() throws Exception {
        doNothing().when(applicationService).withdrawApplication(1L, "Not interested");

        mockMvc.perform(put("/api/applications/1/withdraw")
                .param("reason", "Not interested"))
                .andExpect(status().isOk());
    }
}
