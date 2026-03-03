package com.rev.app.service;

import com.rev.app.dto.JobSeekerDTO;
import com.rev.app.entity.JobSeeker;
import com.rev.app.exception.ResourceNotFoundException;
import com.rev.app.mapper.JobSeekerMapper;
import com.rev.app.repository.JobSeekerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@Transactional
public class JobSeekerServiceImpl implements JobSeekerService {

    @Autowired
    private JobSeekerRepository jobSeekerRepository;

    @Autowired
    private JobSeekerMapper mapper;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private SavedJobService savedJobService;

    @Autowired
    private NotificationService notificationService;

    @Override
    public JobSeekerDTO getProfileByUserId(Long userId) {
        JobSeeker seeker = jobSeekerRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Job Seeker profile not found"));
        return mapper.toJobSeekerDTO(seeker);
    }

    @Override
    public JobSeekerDTO getProfileById(Long id) {
        JobSeeker seeker = jobSeekerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job Seeker not found"));
        return mapper.toJobSeekerDTO(seeker);
    }

    @Override
    public JobSeekerDTO updateProfile(Long id, JobSeekerDTO dto) {
        JobSeeker seeker = jobSeekerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job Seeker not found"));

        seeker.setName(dto.getName());
        seeker.setPhone(dto.getPhone());
        seeker.setLocation(dto.getLocation());
        seeker.setEmploymentStatus(dto.getEmploymentStatus());

        return mapper.toJobSeekerDTO(jobSeekerRepository.save(seeker));
    }

    @Override
    public Map<String, Object> getDashboardSummary(Long seekerId) {
        Map<String, Object> summary = new HashMap<>();
        summary.put("applicationCount", applicationService.getApplicationsBySeeker(seekerId).size());
        summary.put("savedJobCount", savedJobService.getSavedJobsBySeeker(seekerId).size());

        JobSeeker seeker = jobSeekerRepository.findById(seekerId)
                .orElseThrow(() -> new ResourceNotFoundException("Job Seeker not found"));
        summary.put("unreadNotificationCount", notificationService.getUnreadCount(seeker.getUser().getId()));

        return summary;
    }
}
