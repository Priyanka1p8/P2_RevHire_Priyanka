package com.rev.app.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rev.app.config.JwtUtil;
import com.rev.app.dto.JobDTO;
import com.rev.app.service.JobService;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(JobRestController.class)
@AutoConfigureMockMvc(addFilters = false)
public class JobRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JobService jobService;

    @MockBean
    private UserDetailsService userDetailsService;

    @MockBean
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testGetAllJobs() throws Exception {
        when(jobService.getAllActiveJobs()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/jobs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    public void testGetJobById() throws Exception {
        JobDTO dto = new JobDTO();
        dto.setId(1L);
        dto.setTitle("Engineer");

        when(jobService.getJobById(1L)).thenReturn(dto);

        mockMvc.perform(get("/api/jobs/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Engineer"));
    }

    @Test
    public void testCreateJob() throws Exception {
        JobDTO dto = new JobDTO();
        dto.setTitle("New Job");

        when(jobService.createJob(any())).thenReturn(dto);

        mockMvc.perform(post("/api/jobs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("New Job"));
    }

    @Test
    public void testSearchJobs() throws Exception {
        when(jobService.searchJobs("Java")).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/jobs").param("keyword", "Java"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        verify(jobService, times(1)).searchJobs("Java");
    }

    @Test
    public void testUpdateJob() throws Exception {
        JobDTO dto = new JobDTO();
        dto.setTitle("Updated Job");

        when(jobService.updateJob(eq(1L), any())).thenReturn(dto);

        mockMvc.perform(put("/api/jobs/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Job"));
    }

    @Test
    public void testDeleteJob() throws Exception {
        doNothing().when(jobService).deleteJob(1L);

        mockMvc.perform(delete("/api/jobs/1"))
                .andExpect(status().isNoContent());

        verify(jobService, times(1)).deleteJob(1L);
    }

    @Test
    public void testCloseJob() throws Exception {
        doNothing().when(jobService).closeJob(1L);

        mockMvc.perform(put("/api/jobs/1/close"))
                .andExpect(status().isOk());

        verify(jobService, times(1)).closeJob(1L);
    }
}
