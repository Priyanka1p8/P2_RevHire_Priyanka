package com.rev.app.service;

import com.rev.app.dto.SavedJobDTO;
import com.rev.app.entity.Job;
import com.rev.app.entity.JobSeeker;
import com.rev.app.entity.SavedJob;
import com.rev.app.exception.ResourceNotFoundException;
import com.rev.app.mapper.AppMapper;
import com.rev.app.repository.JobRepository;
import com.rev.app.repository.JobSeekerRepository;
import com.rev.app.repository.SavedJobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SavedJobServiceImpl implements SavedJobService {

    @Autowired
    private SavedJobRepository savedJobRepository;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private JobSeekerRepository jobSeekerRepository;

    @Autowired
    private AppMapper mapper;

    @Override
    public void saveJob(Long seekerId, Long jobId) {
        JobSeeker seeker = jobSeekerRepository.findById(seekerId)
                .orElseThrow(() -> new ResourceNotFoundException("Job Seeker not found"));
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found"));

        if (!savedJobRepository.existsByJobSeekerAndJob(seeker, job)) {
            SavedJob savedJob = new SavedJob();
            savedJob.setJobSeeker(seeker);
            savedJob.setJob(job);
            savedJobRepository.save(savedJob);
        }
    }

    @Override
    public void unsaveJob(Long seekerId, Long jobId) {
        JobSeeker seeker = jobSeekerRepository.findById(seekerId)
                .orElseThrow(() -> new ResourceNotFoundException("Job Seeker not found"));
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found"));

        savedJobRepository.findByJobSeekerAndJob(seeker, job)
                .ifPresent(savedJobRepository::delete);
    }

    @Override
    public List<SavedJobDTO> getSavedJobsBySeeker(Long seekerId) {
        JobSeeker seeker = jobSeekerRepository.findById(seekerId)
                .orElseThrow(() -> new ResourceNotFoundException("Job Seeker not found"));
        return savedJobRepository.findByJobSeeker(seeker).stream()
                .map(mapper::toSavedJobDTO)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isJobSaved(Long seekerId, Long jobId) {
        JobSeeker seeker = jobSeekerRepository.findById(seekerId)
                .orElseThrow(() -> new ResourceNotFoundException("Job Seeker not found"));
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found"));
        return savedJobRepository.existsByJobSeekerAndJob(seeker, job);
    }
}
