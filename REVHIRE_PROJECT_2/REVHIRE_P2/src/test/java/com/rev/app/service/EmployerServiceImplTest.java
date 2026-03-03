package com.rev.app.service;

import com.rev.app.dto.EmployerDTO;
import com.rev.app.entity.Employer;
import com.rev.app.exception.ResourceNotFoundException;
import com.rev.app.mapper.EmployerMapper;
import com.rev.app.repository.CompanyRepository;
import com.rev.app.repository.EmployerRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class EmployerServiceImplTest {

    @Mock
    private EmployerRepository employerRepository;

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private EmployerMapper mapper;

    @Mock
    private JobService jobService;

    @InjectMocks
    private EmployerServiceImpl employerService;

    @Test
    public void testGetProfileByUserId_Success() {
        Employer employer = new Employer();
        employer.setId(1L);
        when(employerRepository.findByUserId(anyLong())).thenReturn(Optional.of(employer));
        when(mapper.toEmployerDTO(employer)).thenReturn(new EmployerDTO());

        EmployerDTO result = employerService.getProfileByUserId(1L);

        assertThat(result).isNotNull();
    }

    @Test(expected = ResourceNotFoundException.class)
    public void testGetProfileByUserId_NotFound() {
        when(employerRepository.findByUserId(anyLong())).thenReturn(Optional.empty());
        employerService.getProfileByUserId(1L);
    }

    @Test
    public void testGetProfileById_Success() {
        Employer employer = new Employer();
        employer.setId(1L);
        when(employerRepository.findById(1L)).thenReturn(Optional.of(employer));
        when(mapper.toEmployerDTO(employer)).thenReturn(new EmployerDTO());

        EmployerDTO result = employerService.getProfileById(1L);

        assertThat(result).isNotNull();
    }

    @Test
    public void testUpdateProfile() {
        Employer employer = new Employer();
        employer.setId(1L);
        EmployerDTO dto = new EmployerDTO();
        dto.setContactPerson("New Contact");

        when(employerRepository.findById(1L)).thenReturn(Optional.of(employer));
        when(employerRepository.save(any(Employer.class))).thenReturn(employer);
        when(mapper.toEmployerDTO(any())).thenReturn(dto);

        EmployerDTO result = employerService.updateProfile(1L, dto);

        assertThat(result.getContactPerson()).isEqualTo("New Contact");
        verify(employerRepository).save(employer);
    }

    @Test
    public void testGetStatistics() {
        Long employerId = 1L;
        when(jobService.getJobsByEmployer(employerId)).thenReturn(java.util.Collections.emptyList());

        Map<String, Object> stats = employerService.getStatistics(employerId);

        assertThat(stats).containsEntry("totalJobs", 0L);
        assertThat(stats).containsEntry("activeJobs", 0L);
    }
}
