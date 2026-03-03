package com.rev.app.rest;

import com.rev.app.dto.ResumeDTO;
import com.rev.app.service.ResumeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/resumes")
public class ResumeRestController {

    private final ResumeService resumeService;

    @Autowired
    public ResumeRestController(ResumeService resumeService) {
        this.resumeService = resumeService;
    }

    @PostMapping("/upload/{seekerId}")
    public ResponseEntity<String> uploadResume(@PathVariable Long seekerId, @RequestParam("file") MultipartFile file) {
        try {
            resumeService.uploadResumeFile(seekerId, file);
            return ResponseEntity.ok("Resume uploaded successfully");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Upload failed: " + e.getMessage());
        }
    }

    @GetMapping("/seeker/{seekerId}")
    public ResponseEntity<ResumeDTO> getResume(@PathVariable Long seekerId) {
        return ResponseEntity.ok(resumeService.getResumeBySeekerId(seekerId));
    }

    @PostMapping
    public ResponseEntity<ResumeDTO> saveResume(@RequestBody ResumeDTO resumeDTO) {
        return ResponseEntity.ok(resumeService.createOrUpdateResume(resumeDTO));
    }

    @DeleteMapping("/{seekerId}")
    public ResponseEntity<Void> deleteResume(@PathVariable Long seekerId) {
        resumeService.deleteResumeFile(seekerId);
        return ResponseEntity.noContent().build();
    }
}
