package com.rev.app.rest;

import com.rev.app.service.EmployerService;
import com.rev.app.service.JobSeekerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardRestController {

    private final JobSeekerService jobSeekerService;
    private final EmployerService employerService;

    @Autowired
    public DashboardRestController(JobSeekerService jobSeekerService, EmployerService employerService) {
        this.jobSeekerService = jobSeekerService;
        this.employerService = employerService;
    }

    @GetMapping("/seeker/{seekerId}")
    public ResponseEntity<Map<String, Object>> getSeekerDashboard(@PathVariable Long seekerId) {
        return ResponseEntity.ok(jobSeekerService.getDashboardSummary(seekerId));
    }

    @GetMapping("/employer/{employerId}")
    public ResponseEntity<Map<String, Object>> getEmployerDashboard(@PathVariable Long employerId) {
        return ResponseEntity.ok(employerService.getStatistics(employerId));
    }
}
