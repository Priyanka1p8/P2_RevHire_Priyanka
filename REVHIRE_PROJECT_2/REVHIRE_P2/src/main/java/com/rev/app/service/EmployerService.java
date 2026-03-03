package com.rev.app.service;

import com.rev.app.dto.EmployerDTO;
import java.util.Map;

public interface EmployerService {
    EmployerDTO getProfileByUserId(Long userId);

    EmployerDTO getProfileById(Long id);

    EmployerDTO updateProfile(Long id, EmployerDTO dto);

    Map<String, Object> getStatistics(Long employerId);
}
