package com.rev.app.rest;

import com.rev.app.dto.JobSeekerDTO;
import com.rev.app.service.JobSeekerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/api/seeker")
public class JobSeekerRestController {

    private final JobSeekerService jobSeekerService;

    @Autowired
    public JobSeekerRestController(JobSeekerService jobSeekerService) {
        this.jobSeekerService = jobSeekerService;
    }

    @GetMapping("/profile/{userId}")
    public ResponseEntity<JobSeekerDTO> getProfile(@PathVariable Long userId) {
        return ResponseEntity.ok(jobSeekerService.getProfileByUserId(userId));
    }

    @PutMapping("/profile/{id}")
    public ResponseEntity<JobSeekerDTO> updateProfile(@PathVariable Long id, @Valid @RequestBody JobSeekerDTO dto) {
        return ResponseEntity.ok(jobSeekerService.updateProfile(id, dto));
    }

    @GetMapping("/{id}/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboardData(@PathVariable Long id) {
        return ResponseEntity.ok(jobSeekerService.getDashboardSummary(id));
    }
}
