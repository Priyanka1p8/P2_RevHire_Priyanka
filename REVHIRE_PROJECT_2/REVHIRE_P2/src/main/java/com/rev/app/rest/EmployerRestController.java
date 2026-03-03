package com.rev.app.rest;

import com.rev.app.dto.EmployerDTO;
import com.rev.app.service.EmployerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/api/employer")
public class EmployerRestController {

    private final EmployerService employerService;

    @Autowired
    public EmployerRestController(EmployerService employerService) {
        this.employerService = employerService;
    }

    @GetMapping("/profile/{userId}")
    public ResponseEntity<EmployerDTO> getProfile(@PathVariable Long userId) {
        return ResponseEntity.ok(employerService.getProfileByUserId(userId));
    }

    @PutMapping("/profile/{id}")
    public ResponseEntity<EmployerDTO> updateProfile(@PathVariable Long id, @Valid @RequestBody EmployerDTO dto) {
        return ResponseEntity.ok(employerService.updateProfile(id, dto));
    }

    @GetMapping("/{id}/stats")
    public ResponseEntity<Map<String, Object>> getStatistics(@PathVariable Long id) {
        return ResponseEntity.ok(employerService.getStatistics(id));
    }
}
