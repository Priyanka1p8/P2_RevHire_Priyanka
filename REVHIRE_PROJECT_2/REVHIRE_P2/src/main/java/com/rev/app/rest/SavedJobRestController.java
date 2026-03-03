package com.rev.app.rest;

import com.rev.app.dto.SavedJobDTO;
import com.rev.app.service.SavedJobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/saved-jobs")
public class SavedJobRestController {

    private final SavedJobService savedJobService;

    @Autowired
    public SavedJobRestController(SavedJobService savedJobService) {
        this.savedJobService = savedJobService;
    }

    @PostMapping("/{seekerId}/{jobId}")
    public ResponseEntity<Void> saveJob(@PathVariable Long seekerId, @PathVariable Long jobId) {
        savedJobService.saveJob(seekerId, jobId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{seekerId}/{jobId}")
    public ResponseEntity<Void> unsaveJob(@PathVariable Long seekerId, @PathVariable Long jobId) {
        savedJobService.unsaveJob(seekerId, jobId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/seeker/{seekerId}")
    public ResponseEntity<List<SavedJobDTO>> getSavedJobs(@PathVariable Long seekerId) {
        return ResponseEntity.ok(savedJobService.getSavedJobsBySeeker(seekerId));
    }

    @GetMapping("/check")
    public ResponseEntity<Boolean> isJobSaved(@RequestParam Long seekerId, @RequestParam Long jobId) {
        return ResponseEntity.ok(savedJobService.isJobSaved(seekerId, jobId));
    }
}
