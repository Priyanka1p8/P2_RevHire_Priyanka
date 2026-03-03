package com.rev.app.rest;

import com.rev.app.config.JwtUtil;
import com.rev.app.service.EmployerService;
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

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@RunWith(SpringRunner.class)
@WebMvcTest(DashboardRestController.class)
@AutoConfigureMockMvc(addFilters = false)
public class DashboardRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JobSeekerService jobSeekerService;

    @MockBean
    private EmployerService employerService;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private UserDetailsService userDetailsService;

    @MockBean
    private JwtUtil jwtUtil;

    @Test
    public void testGetSeekerDashboard() throws Exception {
        Long seekerId = 1L;
        Map<String, Object> summary = new HashMap<>();
        summary.put("applications", 5);
        summary.put("savedJobs", 3);

        when(jobSeekerService.getDashboardSummary(seekerId)).thenReturn(summary);

        mockMvc.perform(get("/api/dashboard/seeker/{seekerId}", seekerId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.applications").value(5))
                .andExpect(jsonPath("$.savedJobs").value(3));
    }

    @Test
    public void testGetEmployerDashboard() throws Exception {
        Long employerId = 1L;
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalJobs", 10);
        stats.put("totalApplications", 50);

        when(employerService.getStatistics(employerId)).thenReturn(stats);

        mockMvc.perform(get("/api/dashboard/employer/{employerId}", employerId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalJobs").value(10))
                .andExpect(jsonPath("$.totalApplications").value(50));
    }
}
