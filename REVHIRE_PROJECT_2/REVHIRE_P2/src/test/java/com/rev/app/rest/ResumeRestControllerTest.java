package com.rev.app.rest;

import com.rev.app.config.JwtUtil;
import com.rev.app.dto.ResumeDTO;
import com.rev.app.service.ResumeService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(ResumeRestController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ResumeRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ResumeService resumeService;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private UserDetailsService userDetailsService;

    @MockBean
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testUploadResume_Success() throws Exception {
        Long seekerId = 1L;
        MockMultipartFile file = new MockMultipartFile("file", "resume.pdf", "application/pdf",
                "test content".getBytes());

        doNothing().when(resumeService).uploadResumeFile(eq(seekerId), any());

        mockMvc.perform(multipart("/api/resumes/upload/{seekerId}", seekerId).file(file))
                .andExpect(status().isOk())
                .andExpect(content().string("Resume uploaded successfully"));
    }

    @Test
    public void testGetResume() throws Exception {
        Long seekerId = 1L;
        ResumeDTO dto = new ResumeDTO();
        dto.setJobSeekerId(seekerId);

        when(resumeService.getResumeBySeekerId(seekerId)).thenReturn(dto);

        mockMvc.perform(get("/api/resumes/seeker/{seekerId}", seekerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jobSeekerId").value(seekerId));
    }

    @Test
    public void testSaveResume() throws Exception {
        ResumeDTO dto = new ResumeDTO();
        dto.setJobSeekerId(1L);

        when(resumeService.createOrUpdateResume(any(ResumeDTO.class))).thenReturn(dto);

        mockMvc.perform(post("/api/resumes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jobSeekerId").value(1L));
    }

    @Test
    public void testDeleteResume() throws Exception {
        Long seekerId = 1L;
        doNothing().when(resumeService).deleteResumeFile(seekerId);

        mockMvc.perform(delete("/api/resumes/{seekerId}", seekerId))
                .andExpect(status().isNoContent());

        verify(resumeService, times(1)).deleteResumeFile(seekerId);
    }
}
