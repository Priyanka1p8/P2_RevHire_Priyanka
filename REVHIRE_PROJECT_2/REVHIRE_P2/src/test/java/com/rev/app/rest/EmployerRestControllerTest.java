package com.rev.app.rest;

import com.rev.app.config.JwtUtil;
import com.rev.app.dto.EmployerDTO;
import com.rev.app.service.EmployerService;
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
@WebMvcTest(EmployerRestController.class)
@AutoConfigureMockMvc(addFilters = false)
public class EmployerRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmployerService employerService;

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
        EmployerDTO dto = new EmployerDTO();
        dto.setContactPerson("Test Contact");

        when(employerService.getProfileByUserId(userId)).thenReturn(dto);

        mockMvc.perform(get("/api/employer/profile/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.contactPerson").value("Test Contact"));
    }

    @Test
    public void testUpdateProfile() throws Exception {
        Long employerId = 1L;
        EmployerDTO dto = new EmployerDTO();
        dto.setContactPerson("Updated Contact");

        when(employerService.updateProfile(eq(employerId), any(EmployerDTO.class))).thenReturn(dto);

        mockMvc.perform(put("/api/employer/profile/{id}", employerId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.contactPerson").value("Updated Contact"));
    }

    @Test
    public void testGetStatistics() throws Exception {
        Long employerId = 1L;
        Map<String, Object> stats = new HashMap<>();
        stats.put("activeJobs", 5);

        when(employerService.getStatistics(employerId)).thenReturn(stats);

        mockMvc.perform(get("/api/employer/{id}/stats", employerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.activeJobs").value(5));
    }
}
