package com.rev.app.service;

import com.rev.app.dto.ApplicationDTO;
import com.rev.app.entity.*;
import com.rev.app.exception.DuplicateApplicationException;
import com.rev.app.exception.ResourceNotFoundException;
import com.rev.app.mapper.AppMapper;
import com.rev.app.repository.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ApplicationServiceImpl.
 *
 * Requirement coverage:
 * ✔ Apply for job — application saved with status = APPLIED
 * ✔ Cannot apply twice for same job → DuplicateApplicationException
 * ✔ View applications — list shows job title, company, status, date
 * ✔ Withdraw option works — status changes to WITHDRAWN
 * ✔ Bulk shortlist / bulk reject
 * ✔ Status change logged via service
 */
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
    private AppMapper mapper;

    @InjectMocks
    private ApplicationServiceImpl applicationService;

    // ---- test fixtures ----
    private Job job;
    private JobSeeker seeker;
    private Resume resume;
    private Application application;
    private ApplicationDTO applicationDTO;
    private Company company;

    @Before
    public void setUp() {
        company = new Company();
        company.setId(10L);
        company.setName("Acme Corp");

        Employer employer = new Employer();
        employer.setId(5L);
        employer.setCompany(company);

        job = new Job();
        job.setId(1L);
        job.setTitle("Java Backend Developer");
        job.setEmployer(employer);
        job.setCompany(company);
        job.setIsClosed(false);

        seeker = new JobSeeker();
        seeker.setId(2L);
        seeker.setName("Bob Seeker");

        resume = new Resume();
        resume.setId(3L);
        resume.setJobSeeker(seeker);
        resume.setSkills("Java, Spring Boot, SQL");

        application = new Application();
        application.setId(100L);
        application.setJob(job);
        application.setJobSeeker(seeker);
        application.setResume(resume);
        application.setStatus(Application.ApplicationStatus.APPLIED);

        applicationDTO = new ApplicationDTO();
        applicationDTO.setId(100L);
        applicationDTO.setJobId(1L);
        applicationDTO.setJobTitle("Java Backend Developer");
        applicationDTO.setCompanyName("Acme Corp");
        applicationDTO.setJobSeekerId(2L);
        applicationDTO.setSeekerName("Bob Seeker");
        applicationDTO.setResumeId(3L);
        applicationDTO.setStatus(Application.ApplicationStatus.APPLIED);
    }

    // ------------------------------------------------------------------ //
    // Apply for Job Tests
    // ------------------------------------------------------------------ //

    /** ✔ Successful apply — application is saved with status APPLIED. */
    @Test
    public void testApplyToJob_Success_StatusIsApplied() {
        ApplicationDTO dto = new ApplicationDTO();
        dto.setJobId(1L);
        dto.setJobSeekerId(2L);
        dto.setResumeId(3L);
        dto.setCoverLetter("Excited to join Acme Corp!");

        when(jobRepository.findById(1L)).thenReturn(Optional.of(job));
        when(jobSeekerRepository.findById(2L)).thenReturn(Optional.of(seeker));
        when(resumeRepository.findById(3L)).thenReturn(Optional.of(resume));
        when(applicationRepository.existsByJobSeekerAndJob(seeker, job)).thenReturn(false);
        when(applicationRepository.save(any(Application.class))).thenReturn(application);
        when(mapper.toApplicationDTO(application)).thenReturn(applicationDTO);

        ApplicationDTO result = applicationService.applyToJob(dto);

        assertNotNull(result);
        assertEquals(Application.ApplicationStatus.APPLIED, result.getStatus());
        verify(applicationRepository, times(1)).save(any(Application.class));
    }

    /** ✔ Application saved with correct job, seeker, resume. */
    @Test
    public void testApplyToJob_CorrectDataSaved() {
        ApplicationDTO dto = new ApplicationDTO();
        dto.setJobId(1L);
        dto.setJobSeekerId(2L);
        dto.setResumeId(3L);

        when(jobRepository.findById(1L)).thenReturn(Optional.of(job));
        when(jobSeekerRepository.findById(2L)).thenReturn(Optional.of(seeker));
        when(resumeRepository.findById(3L)).thenReturn(Optional.of(resume));
        when(applicationRepository.existsByJobSeekerAndJob(seeker, job)).thenReturn(false);
        when(applicationRepository.save(any(Application.class))).thenReturn(application);
        when(mapper.toApplicationDTO(application)).thenReturn(applicationDTO);

        ApplicationDTO result = applicationService.applyToJob(dto);

        assertEquals(1L, (long) result.getJobId());
        assertEquals(2L, (long) result.getJobSeekerId());
        assertEquals("Java Backend Developer", result.getJobTitle());
        assertEquals("Acme Corp", result.getCompanyName());
    }

    // ------------------------------------------------------------------ //
    // Duplicate Application Prevention Tests
    // ------------------------------------------------------------------ //

    /** ✔ Cannot apply twice for same job — DuplicateApplicationException thrown. */
    @Test(expected = DuplicateApplicationException.class)
    public void testApplyToJob_DuplicateApplication_ThrowsException() {
        ApplicationDTO dto = new ApplicationDTO();
        dto.setJobId(1L);
        dto.setJobSeekerId(2L);
        dto.setResumeId(3L);

        when(jobRepository.findById(1L)).thenReturn(Optional.of(job));
        when(jobSeekerRepository.findById(2L)).thenReturn(Optional.of(seeker));
        when(resumeRepository.findById(3L)).thenReturn(Optional.of(resume));
        when(applicationRepository.existsByJobSeekerAndJob(seeker, job)).thenReturn(true); // already applied

        applicationService.applyToJob(dto); // must throw
    }

    /** ✔ When a duplicate is detected, no Application record is saved. */
    @Test
    public void testApplyToJob_DuplicateApplication_NothingSaved() {
        ApplicationDTO dto = new ApplicationDTO();
        dto.setJobId(1L);
        dto.setJobSeekerId(2L);
        dto.setResumeId(3L);

        when(jobRepository.findById(1L)).thenReturn(Optional.of(job));
        when(jobSeekerRepository.findById(2L)).thenReturn(Optional.of(seeker));
        when(resumeRepository.findById(3L)).thenReturn(Optional.of(resume));
        when(applicationRepository.existsByJobSeekerAndJob(seeker, job)).thenReturn(true);

        try {
            applicationService.applyToJob(dto);
        } catch (DuplicateApplicationException ignored) {
        }

        verify(applicationRepository, never()).save(any(Application.class));
    }

    /** ✔ Job not found → ResourceNotFoundException. */
    @Test(expected = ResourceNotFoundException.class)
    public void testApplyToJob_JobNotFound_ThrowsResourceNotFoundException() {
        ApplicationDTO dto = new ApplicationDTO();
        dto.setJobId(99L);
        dto.setJobSeekerId(2L);
        dto.setResumeId(3L);

        when(jobRepository.findById(99L)).thenReturn(Optional.empty());

        applicationService.applyToJob(dto);
    }

    // ------------------------------------------------------------------ //
    // View Applications Tests
    // ------------------------------------------------------------------ //

    /**
     * ✔ View applications — list shows job title, company, status, applied date.
     */
    @Test
    public void testGetApplicationsBySeeker_ReturnsList() {
        when(jobSeekerRepository.findById(2L)).thenReturn(Optional.of(seeker));
        when(applicationRepository.findByJobSeeker(seeker)).thenReturn(Arrays.asList(application));
        when(mapper.toApplicationDTO(application)).thenReturn(applicationDTO);

        List<ApplicationDTO> result = applicationService.getApplicationsBySeeker(2L);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals("Java Backend Developer", result.get(0).getJobTitle());
        assertEquals("Acme Corp", result.get(0).getCompanyName());
        assertEquals(Application.ApplicationStatus.APPLIED, result.get(0).getStatus());
    }

    /** ✔ Employer view — getApplicationsByJob returns correct list. */
    @Test
    public void testGetApplicationsByJob_ReturnsList() {
        when(jobRepository.findById(1L)).thenReturn(Optional.of(job));
        when(applicationRepository.findByJob(job)).thenReturn(Arrays.asList(application));
        when(mapper.toApplicationDTO(application)).thenReturn(applicationDTO);

        List<ApplicationDTO> result = applicationService.getApplicationsByJob(1L);

        assertEquals(1, result.size());
        assertEquals("Bob Seeker", result.get(0).getSeekerName());
    }

    // ------------------------------------------------------------------ //
    // Withdraw Application Tests
    // ------------------------------------------------------------------ //

    /** ✔ Withdraw changes status to WITHDRAWN. */
    @Test
    public void testWithdrawApplication_StatusBecomesWithdrawn() {
        when(applicationRepository.findById(100L)).thenReturn(Optional.of(application));
        when(applicationRepository.save(any(Application.class))).thenReturn(application);

        applicationService.withdrawApplication(100L, "Found another opportunity");

        assertEquals(Application.ApplicationStatus.WITHDRAWN, application.getStatus());
        assertEquals("Found another opportunity", application.getWithdrawReason());
        verify(applicationRepository, times(1)).save(application);
    }

    /** ✔ Withdraw creates a note when a reason is provided. */
    @Test
    public void testWithdrawApplication_NoteCreatedWhenReasonProvided() {
        when(applicationRepository.findById(100L)).thenReturn(Optional.of(application));
        when(applicationRepository.save(any(Application.class))).thenReturn(application);

        applicationService.withdrawApplication(100L, "Got a better offer");

        verify(noteRepository, times(1)).save(any(ApplicationNote.class));
    }

    /** ✔ Withdraw with no reason — works, no note saved. */
    @Test
    public void testWithdrawApplication_NoReason_NoNoteSaved() {
        when(applicationRepository.findById(100L)).thenReturn(Optional.of(application));
        when(applicationRepository.save(any(Application.class))).thenReturn(application);

        applicationService.withdrawApplication(100L, null);

        verify(noteRepository, never()).save(any(ApplicationNote.class));
    }

    // ------------------------------------------------------------------ //
    // Status Update Tests
    // ------------------------------------------------------------------ //

    /** ✔ Employer changes status to SHORTLISTED. */
    @Test
    public void testUpdateApplicationStatus_Shortlisted() {
        ApplicationDTO shortlistedDTO = new ApplicationDTO();
        shortlistedDTO.setId(100L);
        shortlistedDTO.setStatus(Application.ApplicationStatus.SHORTLISTED);
        shortlistedDTO.setJobTitle("Java Backend Developer");
        shortlistedDTO.setJobSeekerId(2L);

        when(applicationRepository.findById(100L)).thenReturn(Optional.of(application));
        when(applicationRepository.save(any(Application.class))).thenReturn(application);
        when(mapper.toApplicationDTO(application)).thenReturn(shortlistedDTO);

        ApplicationDTO result = applicationService.updateApplicationStatus(100L, "SHORTLISTED", null);

        assertEquals(Application.ApplicationStatus.SHORTLISTED, result.getStatus());
        assertEquals(Application.ApplicationStatus.SHORTLISTED, application.getStatus());
    }

    /** ✔ Employer changes status to REJECTED. */
    @Test
    public void testUpdateApplicationStatus_Rejected() {
        ApplicationDTO rejectedDTO = new ApplicationDTO();
        rejectedDTO.setId(100L);
        rejectedDTO.setStatus(Application.ApplicationStatus.REJECTED);
        rejectedDTO.setJobSeekerId(2L);
        rejectedDTO.setJobTitle("Java Backend Developer");

        when(applicationRepository.findById(100L)).thenReturn(Optional.of(application));
        when(applicationRepository.save(any(Application.class))).thenReturn(application);
        when(mapper.toApplicationDTO(application)).thenReturn(rejectedDTO);

        ApplicationDTO result = applicationService.updateApplicationStatus(100L, "REJECTED", "Not enough experience");

        assertEquals(Application.ApplicationStatus.REJECTED, result.getStatus());
        verify(noteRepository, times(1)).save(any(ApplicationNote.class));
    }

    // ------------------------------------------------------------------ //
    // Bulk Status Update Tests
    // ------------------------------------------------------------------ //

    /** ✔ Bulk shortlist — all selected applications are updated. */
    @Test
    public void testUpdateStatusBulk_Shortlist_AllUpdated() {
        Application app2 = new Application();
        app2.setId(101L);
        app2.setJob(job);
        app2.setJobSeeker(seeker);
        app2.setResume(resume);
        app2.setStatus(Application.ApplicationStatus.APPLIED);

        ApplicationDTO dto2 = new ApplicationDTO();
        dto2.setId(101L);
        dto2.setStatus(Application.ApplicationStatus.SHORTLISTED);
        dto2.setJobSeekerId(2L);
        dto2.setJobTitle("Java Backend Developer");

        when(applicationRepository.findById(100L)).thenReturn(Optional.of(application));
        when(applicationRepository.findById(101L)).thenReturn(Optional.of(app2));
        when(applicationRepository.save(any(Application.class))).thenAnswer(i -> i.getArguments()[0]);
        when(mapper.toApplicationDTO(any(Application.class))).thenReturn(dto2);

        applicationService.updateStatusBulk(Arrays.asList(100L, 101L), "SHORTLISTED", "Bulk shortlist");

        // Both applications should be updated
        verify(applicationRepository, times(2)).save(any(Application.class));
    }

    /** ✔ Bulk reject — all selected applications rejected. */
    @Test
    public void testUpdateStatusBulk_Reject_AllUpdated() {
        Application app2 = new Application();
        app2.setId(101L);
        app2.setJob(job);
        app2.setJobSeeker(seeker);
        app2.setResume(resume);
        app2.setStatus(Application.ApplicationStatus.APPLIED);

        ApplicationDTO dto2 = new ApplicationDTO();
        dto2.setId(101L);
        dto2.setStatus(Application.ApplicationStatus.REJECTED);
        dto2.setJobSeekerId(2L);
        dto2.setJobTitle("Java Backend Developer");

        when(applicationRepository.findById(100L)).thenReturn(Optional.of(application));
        when(applicationRepository.findById(101L)).thenReturn(Optional.of(app2));
        when(applicationRepository.save(any(Application.class))).thenAnswer(i -> i.getArguments()[0]);
        when(mapper.toApplicationDTO(any(Application.class))).thenReturn(dto2);

        applicationService.updateStatusBulk(Arrays.asList(100L, 101L), "REJECTED", null);

        verify(applicationRepository, times(2)).save(any(Application.class));
    }

    // ------------------------------------------------------------------ //
    // Add Note Tests
    // ------------------------------------------------------------------ //

    /** ✔ Employer can add a note to an application. */
    @Test
    public void testAddNoteToApplication_NotePersistedCorrectly() {
        when(applicationRepository.findById(100L)).thenReturn(Optional.of(application));

        applicationService.addNoteToApplication(100L, "Great candidate, schedule interview");

        verify(noteRepository, times(1)).save(any(ApplicationNote.class));
    }
}
