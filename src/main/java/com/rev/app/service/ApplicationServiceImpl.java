package com.rev.app.service;

import com.rev.app.dto.ApplicationDTO;
import com.rev.app.entity.*;
import com.rev.app.exception.DuplicateApplicationException;
import com.rev.app.exception.ResourceNotFoundException;
import com.rev.app.mapper.AppMapper;
import com.rev.app.repository.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ApplicationServiceImpl implements ApplicationService {

    private static final Logger logger = LogManager.getLogger(ApplicationServiceImpl.class);

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private JobSeekerRepository jobSeekerRepository;

    @Autowired
    private ResumeRepository resumeRepository;

    @Autowired
    private ApplicationNoteRepository noteRepository;

    @Autowired
    private AppMapper mapper;

    @Autowired
    private NotificationService notificationService;

    @Override
    public ApplicationDTO applyToJob(ApplicationDTO dto) {
        Job job = jobRepository.findById(dto.getJobId())
                .orElseThrow(() -> new ResourceNotFoundException("Job not found"));
        JobSeeker seeker = jobSeekerRepository.findById(dto.getJobSeekerId())
                .orElseThrow(() -> new ResourceNotFoundException("Job Seeker not found"));
        Resume resume = resumeRepository.findById(dto.getResumeId())
                .orElseThrow(() -> new ResourceNotFoundException("Resume not found"));

        // ✔ Cannot apply twice for same job
        if (applicationRepository.existsByJobSeekerAndJob(seeker, job)) {
            logger.warn("Seeker {} attempted duplicate application for job {}",
                    seeker.getId(), job.getId());
            throw new DuplicateApplicationException(
                    "You have already applied for this job.");
        }

        Application app = new Application();
        app.setJob(job);
        app.setJobSeeker(seeker);
        app.setResume(resume);
        app.setCoverLetter(dto.getCoverLetter());
        app.setStatus(Application.ApplicationStatus.APPLIED);

        ApplicationDTO saved = mapper.toApplicationDTO(applicationRepository.save(app));
        logger.info("Application submitted: seeker={} job={} status=APPLIED",
                seeker.getId(), job.getId());

        // Notify Employer
        notificationService.sendNotification(job.getEmployer().getUser().getId(),
                "New application received from " + seeker.getName() + " for '" + job.getTitle() + "'");

        return saved;
    }

    @Override
    public List<ApplicationDTO> getApplicationsBySeeker(Long seekerId) {
        JobSeeker seeker = jobSeekerRepository.findById(seekerId)
                .orElseThrow(() -> new ResourceNotFoundException("Job Seeker not found"));
        return applicationRepository.findByJobSeeker(seeker).stream()
                .map(mapper::toApplicationDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ApplicationDTO> getApplicationsByJob(Long jobId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found"));
        return applicationRepository.findByJob(job).stream()
                .map(mapper::toApplicationDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ApplicationDTO updateApplicationStatus(Long id, String status, String comment) {
        Application app = applicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));
        app.setStatus(Application.ApplicationStatus.valueOf(status.toUpperCase()));

        if (comment != null && !comment.isEmpty()) {
            ApplicationNote note = new ApplicationNote();
            note.setApplication(app);
            note.setNote(comment);
            noteRepository.save(note);
        }

        ApplicationDTO updated = mapper.toApplicationDTO(applicationRepository.save(app));
        logger.info("Application {} status updated to {}", id, status);

        // Notify Job Seeker
        notificationService.sendNotification(app.getJobSeeker().getUser().getId(),
                "Your application for '" + app.getJob().getTitle() + "' has been updated to: "
                        + status);

        return updated;
    }

    @Override
    public void withdrawApplication(Long id, String reason) {
        Application app = applicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));
        app.setStatus(Application.ApplicationStatus.WITHDRAWN);
        app.setWithdrawReason(reason);

        if (reason != null && !reason.isEmpty()) {
            ApplicationNote note = new ApplicationNote();
            note.setApplication(app);
            note.setNote("Withdrawal reason: " + reason);
            noteRepository.save(note);
        }

        applicationRepository.save(app);
        logger.info("Application {} withdrawn. Reason: {}", id, reason);

        // Notify Employer
        notificationService.sendNotification(app.getJob().getEmployer().getUser().getId(),
                "Applicant " + app.getJobSeeker().getName() + " has withdrawn their application for '"
                        + app.getJob().getTitle() + "'"
                        + (reason != null ? ": " + reason : ""));
    }

    @Override
    public List<ApplicationDTO> searchApplications(Long jobId, String status, String keyword,
                                                   java.time.LocalDate startDate, Integer minExp) {
        Application.ApplicationStatus appStatus = (status != null && !status.isEmpty())
                ? Application.ApplicationStatus.valueOf(status.toUpperCase())
                : null;
        String keywordParam = (keyword != null && !keyword.isEmpty())
                ? "%" + keyword.toLowerCase() + "%"
                : null;
        return applicationRepository.searchApplications(jobId, appStatus, keywordParam, startDate, minExp)
                .stream()
                .map(mapper::toApplicationDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void updateStatusBulk(List<Long> ids, String status, String comment) {
        for (Long id : ids) {
            updateApplicationStatus(id, status, comment);
        }
        logger.info("Bulk status update: {} applications set to {}", ids.size(), status);
    }

    @Override
    public void addNoteToApplication(Long applicationId, String noteContent) {
        Application app = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));
        ApplicationNote note = new ApplicationNote();
        note.setApplication(app);
        note.setNote(noteContent);
        noteRepository.save(note);
    }

    @Override
    public void updateNote(Long noteId, String newNote) {
        ApplicationNote note = noteRepository.findById(noteId)
                .orElseThrow(() -> new ResourceNotFoundException("Note not found"));
        note.setNote(newNote);
        noteRepository.save(note);
    }

    @Override
    public void deleteNote(Long noteId) {
        noteRepository.deleteById(noteId);
    }

    @Override
    public List<Long> getAppliedJobIds(Long seekerId) {
        JobSeeker seeker = jobSeekerRepository.findById(seekerId)
                .orElseThrow(() -> new ResourceNotFoundException("Job Seeker not found"));
        return applicationRepository.findByJobSeeker(seeker).stream()
                .map(app -> app.getJob().getId())
                .collect(Collectors.toList());
    }

    @Override
    public boolean hasApplied(Long seekerId, Long jobId) {
        JobSeeker seeker = jobSeekerRepository.findById(seekerId)
                .orElseThrow(() -> new ResourceNotFoundException("Job Seeker not found"));
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found"));
        return applicationRepository.existsByJobSeekerAndJob(seeker, job);
    }
}
