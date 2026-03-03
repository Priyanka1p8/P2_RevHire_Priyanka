package com.rev.app.rest;

import com.rev.app.config.JwtUtil;
import com.rev.app.dto.SavedJobDTO;
import com.rev.app.service.SavedJobService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(SavedJobRestController.class)
@AutoConfigureMockMvc(addFilters = false)
public class SavedJobRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SavedJobService savedJobService;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private UserDetailsService userDetailsService;

    @MockBean
    private JwtUtil jwtUtil;

    @Test
    public void testSaveJob() throws Exception {
        Long seekerId = 1L;
        Long jobId = 101L;

        doNothing().when(savedJobService).saveJob(seekerId, jobId);

        mockMvc.perform(post("/api/saved-jobs/{seekerId}/{jobId}", seekerId, jobId))
                .andExpect(status().isOk());

        verify(savedJobService, times(1)).saveJob(seekerId, jobId);
    }

    @Test
    public void testUnsaveJob() throws Exception {
        Long seekerId = 1L;
        Long jobId = 101L;

        doNothing().when(savedJobService).unsaveJob(seekerId, jobId);

        mockMvc.perform(delete("/api/saved-jobs/{seekerId}/{jobId}", seekerId, jobId))
                .andExpect(status().isNoContent());

        verify(savedJobService, times(1)).unsaveJob(seekerId, jobId);
    }

    @Test
    public void testGetSavedJobs() throws Exception {
        Long seekerId = 1L;
        SavedJobDTO dto = new SavedJobDTO();
        dto.setJobId(101L);
        List<SavedJobDTO> savedJobs = Arrays.asList(dto);

        when(savedJobService.getSavedJobsBySeeker(seekerId)).thenReturn(savedJobs);

        mockMvc.perform(get("/api/saved-jobs/seeker/{seekerId}", seekerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].jobId").value(101L));
    }

    @Test
    public void testIsJobSaved() throws Exception {
        Long seekerId = 1L;
        Long jobId = 101L;

        when(savedJobService.isJobSaved(seekerId, jobId)).thenReturn(true);

        mockMvc.perform(get("/api/saved-jobs/check")
                .param("seekerId", seekerId.toString())
                .param("jobId", jobId.toString()))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }
}
