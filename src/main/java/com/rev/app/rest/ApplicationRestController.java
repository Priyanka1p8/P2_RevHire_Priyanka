package com.rev.app.rest;

import com.rev.app.dto.ApplicationDTO;
import com.rev.app.service.ApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/applications")
public class ApplicationRestController {

    private final ApplicationService applicationService;

    @Autowired
    public ApplicationRestController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @PostMapping
    public ResponseEntity<ApplicationDTO> apply(@RequestBody ApplicationDTO dto) {
        return ResponseEntity.ok(applicationService.applyToJob(dto));
    }

    @GetMapping("/seeker/{seekerId}")
    public ResponseEntity<List<ApplicationDTO>> getBySeeker(@PathVariable Long seekerId) {
        return ResponseEntity.ok(applicationService.getApplicationsBySeeker(seekerId));
    }

    @GetMapping("/job/{jobId}")
    public ResponseEntity<List<ApplicationDTO>> getByJob(@PathVariable Long jobId) {
        return ResponseEntity.ok(applicationService.getApplicationsByJob(jobId));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ApplicationDTO> updateStatus(@PathVariable Long id,
                                                       @RequestParam String status,
                                                       @RequestParam(required = false) String comment) {
        return ResponseEntity.ok(applicationService.updateApplicationStatus(id, status, comment));
    }

    @PutMapping("/{id}/withdraw")
    public ResponseEntity<Void> withdraw(@PathVariable Long id,
                                         @RequestParam(required = false) String reason) {
        applicationService.withdrawApplication(id, reason);
        return ResponseEntity.ok().build();
    }
}
