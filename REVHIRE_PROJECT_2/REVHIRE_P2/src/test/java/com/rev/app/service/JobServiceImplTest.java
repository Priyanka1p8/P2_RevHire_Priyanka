package com.rev.app.service;

import com.rev.app.dto.JobDTO;
import com.rev.app.entity.Company;
import com.rev.app.entity.Employer;
import com.rev.app.entity.Job;
import com.rev.app.exception.ResourceNotFoundException;
import com.rev.app.mapper.JobMapper;
import com.rev.app.repository.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class JobServiceImplTest {

    @Mock
    private JobRepository jobRepository;

    @Mock
    private EmployerRepository employerRepository;

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private ApplicationRepository applicationRepository;

    @Mock
    private JobMapper mapper;

    @Mock
    private NotificationService notificationService;

    @Mock
    private JobSeekerRepository jobSeekerRepository;

    @Mock
    private ResumeRepository resumeRepository;

    @InjectMocks
    private JobServiceImpl jobService;

    @Test
    public void testCreateJob_Success() {
        JobDTO dto = new JobDTO();
        dto.setEmployerId(1L);
        dto.setCompanyId(1L);
        dto.setTitle("Test Job");

        Employer employer = new Employer();
        Company company = new Company();
        Job job = new Job();
        job.setTitle("Test Job");
        job.setSkillsRequired("Java");
        job.setCompany(company);

        when(employerRepository.findById(1L)).thenReturn(Optional.of(employer));
        when(companyRepository.findById(1L)).thenReturn(Optional.of(company));
        when(mapper.toJobEntity(eq(dto), any(), any())).thenReturn(job);
        when(jobRepository.save(any())).thenReturn(job);
        when(jobSeekerRepository.findAll()).thenReturn(Collections.emptyList());
        when(mapper.toJobDTO(any())).thenReturn(dto);

        JobDTO result = jobService.createJob(dto);

        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("Test Job");
        verify(jobRepository, times(1)).save(any());
    }

    @Test(expected = ResourceNotFoundException.class)
    public void testGetJobById_NotFound() {
        when(jobRepository.findById(anyLong())).thenReturn(Optional.empty());
        jobService.getJobById(1L);
    }

    @Test
    public void testUpdateJob() {
        Job job = new Job();
        job.setId(1L);
        JobDTO dto = new JobDTO();
        dto.setTitle("Updated Title");

        when(jobRepository.findById(1L)).thenReturn(Optional.of(job));
        when(jobRepository.save(any())).thenReturn(job);
        when(mapper.toJobDTO(any())).thenReturn(dto);

        JobDTO result = jobService.updateJob(1L, dto);

        assertThat(result.getTitle()).isEqualTo("Updated Title");
        verify(jobRepository).save(job);
    }

    @Test
    public void testDeleteJob() {
        jobService.deleteJob(1L);
        verify(jobRepository, times(1)).deleteById(1L);
    }

    @Test
    public void testGetAllActiveJobs() {
        when(jobRepository.findByIsClosedFalse()).thenReturn(Collections.emptyList());
        List<JobDTO> result = jobService.getAllActiveJobs();
        assertThat(result).isEmpty();
    }

    @Test
    public void testGetJobsByEmployer() {
        Employer employer = new Employer();
        when(employerRepository.findById(1L)).thenReturn(Optional.of(employer));
        when(jobRepository.findByEmployer(employer)).thenReturn(Collections.emptyList());

        List<JobDTO> result = jobService.getJobsByEmployer(1L);
        assertThat(result).isEmpty();
    }

    @Test
    public void testSearchJobs() {
        when(jobRepository.searchJobsAdvanced(any(), any(), any(), any(), any(), any()))
                .thenReturn(Collections.emptyList());
        List<JobDTO> result = jobService.searchJobs("Java");
        assertThat(result).isEmpty();
    }

    @Test
    public void testCloseJob() {
        Job job = new Job();
        job.setId(1L);
        job.setIsClosed(false);

        when(jobRepository.findById(1L)).thenReturn(Optional.of(job));
        when(jobRepository.save(any())).thenReturn(job);

        jobService.closeJob(1L);

        assertThat(job.getIsClosed()).isTrue();
        verify(jobRepository, times(1)).save(job);
    }

    @Test
    public void testReopenJob() {
        Job job = new Job();
        job.setId(1L);
        job.setIsClosed(true);

        when(jobRepository.findById(1L)).thenReturn(Optional.of(job));
        when(jobRepository.save(any())).thenReturn(job);

        jobService.reopenJob(1L);

        assertThat(job.getIsClosed()).isFalse();
        verify(jobRepository).save(job);
    }

    @Test
    public void testMarkJobAsFilled() {
        Job job = new Job();
        job.setId(1L);
        job.setStatus("OPEN");

        when(jobRepository.findById(1L)).thenReturn(Optional.of(job));
        when(jobRepository.save(any())).thenReturn(job);

        jobService.markJobAsFilled(1L);

        assertThat(job.getStatus()).isEqualTo("FILLED");
        assertThat(job.getIsClosed()).isTrue();
        verify(jobRepository).save(job);
    }
}
