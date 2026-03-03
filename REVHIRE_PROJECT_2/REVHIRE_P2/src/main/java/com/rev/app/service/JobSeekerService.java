package com.rev.app.service;

import com.rev.app.dto.JobSeekerDTO;
import java.util.Map;

public interface JobSeekerService {
    JobSeekerDTO getProfileByUserId(Long userId);

    JobSeekerDTO getProfileById(Long id);

    JobSeekerDTO updateProfile(Long id, JobSeekerDTO dto);

    Map<String, Object> getDashboardSummary(Long seekerId);
}
