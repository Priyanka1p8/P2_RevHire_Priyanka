package com.rev.app.service;

import com.rev.app.dto.ApplicationDTO;
import com.rev.app.entity.*;
import com.rev.app.exception.DuplicateApplicationException;
import com.rev.app.exception.ResourceNotFoundException;
import com.rev.app.mapper.ApplicationMapper;
import com.rev.app.repository.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ApplicationServiceImplTest {

    @Mock
    private ApplicationRepository applicationRepository;

    @Mock
    private JobRepository jobRepository;

    @Mock
    private JobSeekerRepository jobSeekerRepository;

    @Mock
    private ResumeRepository resumeRepository;

    @Mock
    private ApplicationNoteRepository noteRepository;

    @Mock
    private ApplicationMapper mapper;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private ApplicationServiceImpl applicationService;

    @Test
    public void testApplyToJob_Success() {
        ApplicationDTO dto = new ApplicationDTO();
        dto.setJobId(1L);
        dto.setJobSeekerId(1L);
        dto.setResumeId(1L);

        Job job = new Job();
        job.setId(1L);
        Employer employer = new Employer();
        User employerUser = new User();
        employerUser.setId(2L);
        employer.setUser(employerUser);
        job.setEmployer(employer);
        job.setTitle("Test Job");

        JobSeeker seeker = new JobSeeker();
        seeker.setId(1L);
        seeker.setName("Seeker");

        Resume resume = new Resume();
        resume.setId(1L);

        when(jobRepository.findById(1L)).thenReturn(Optional.of(job));
        when(jobSeekerRepository.findById(1L)).thenReturn(Optional.of(seeker));
        when(resumeRepository.findById(1L)).thenReturn(Optional.of(resume));
        when(applicationRepository.existsByJobSeekerAndJob(seeker, job)).thenReturn(false);
        when(applicationRepository.save(any(Application.class))).thenReturn(new Application());
        when(mapper.toApplicationDTO(any())).thenReturn(dto);

        ApplicationDTO result = applicationService.applyToJob(dto);

        assertThat(result).isNotNull();
        verify(notificationService, times(1)).sendNotification(anyLong(), anyString());
    }

    @Test(expected = DuplicateApplicationException.class)
    public void testApplyToJob_Duplicate() {
        ApplicationDTO dto = new ApplicationDTO();
        dto.setJobId(1L);
        dto.setJobSeekerId(1L);
        dto.setResumeId(1L);

        Job job = new Job();
        JobSeeker seeker = new JobSeeker();
        Resume resume = new Resume();

        when(jobRepository.findById(1L)).thenReturn(Optional.of(job));
        when(jobSeekerRepository.findById(1L)).thenReturn(Optional.of(seeker));
        when(resumeRepository.findById(1L)).thenReturn(Optional.of(resume));
        when(applicationRepository.existsByJobSeekerAndJob(seeker, job)).thenReturn(true);

        applicationService.applyToJob(dto);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void testApplyToJob_JobNotFound() {
        ApplicationDTO dto = new ApplicationDTO();
        dto.setJobId(1L);
        when(jobRepository.findById(1L)).thenReturn(Optional.empty());
        applicationService.applyToJob(dto);
    }

    @Test
    public void testGetApplicationsBySeeker() {
        JobSeeker seeker = new JobSeeker();
        when(jobSeekerRepository.findById(1L)).thenReturn(Optional.of(seeker));
        when(applicationRepository.findByJobSeeker(seeker)).thenReturn(java.util.Collections.emptyList());

        java.util.List<ApplicationDTO> result = applicationService.getApplicationsBySeeker(1L);
        assertThat(result).isEmpty();
    }

    @Test
    public void testGetApplicationsByJob() {
        Job job = new Job();
        when(jobRepository.findById(1L)).thenReturn(Optional.of(job));
        when(applicationRepository.findByJob(job)).thenReturn(java.util.Collections.emptyList());

        java.util.List<ApplicationDTO> result = applicationService.getApplicationsByJob(1L);
        assertThat(result).isEmpty();
    }

    @Test
    public void testUpdateApplicationStatus() {
        Application app = new Application();
        app.setId(1L);
        Job job = new Job();
        job.setTitle("Test Job");
        app.setJob(job);
        JobSeeker seeker = new JobSeeker();
        User user = new User();
        user.setId(1L);
        seeker.setUser(user);
        app.setJobSeeker(seeker);

        when(applicationRepository.findById(1L)).thenReturn(Optional.of(app));
        when(applicationRepository.save(any(Application.class))).thenReturn(app);
        when(mapper.toApplicationDTO(any())).thenReturn(new ApplicationDTO());

        ApplicationDTO result = applicationService.updateApplicationStatus(1L, "SHORTLISTED", "Good profile");

        assertThat(result).isNotNull();
        verify(noteRepository, times(1)).save(any(ApplicationNote.class));
        verify(notificationService).sendNotification(eq(1L), contains("SHORTLISTED"));
    }

    @Test
    public void testWithdrawApplication() {
        Application app = new Application();
        app.setId(1L);
        Job job = new Job();
        job.setTitle("Test Job");
        Employer employer = new Employer();
        User empUser = new User();
        empUser.setId(2L);
        employer.setUser(empUser);
        job.setEmployer(employer);
        app.setJob(job);
        JobSeeker seeker = new JobSeeker();
        seeker.setName("John");
        app.setJobSeeker(seeker);

        when(applicationRepository.findById(1L)).thenReturn(Optional.of(app));

        applicationService.withdrawApplication(1L, "Found another job");

        assertThat(app.getStatus()).isEqualTo(Application.ApplicationStatus.WITHDRAWN);
        verify(noteRepository).save(any(ApplicationNote.class));
        verify(notificationService).sendNotification(eq(2L), contains("withdrawn"));
    }

    @Test
    public void testHasApplied() {
        JobSeeker seeker = new JobSeeker();
        Job job = new Job();
        when(jobSeekerRepository.findById(1L)).thenReturn(Optional.of(seeker));
        when(jobRepository.findById(1L)).thenReturn(Optional.of(job));
        when(applicationRepository.existsByJobSeekerAndJob(seeker, job)).thenReturn(true);

        boolean result = applicationService.hasApplied(1L, 1L);
        assertThat(result).isTrue();
    }
}
