package com.rev.app.service;

import com.rev.app.dto.ResumeDTO;
import com.rev.app.entity.JobSeeker;
import com.rev.app.entity.Resume;
import com.rev.app.exception.ResourceNotFoundException;
import com.rev.app.mapper.JobSeekerMapper;
import com.rev.app.repository.JobSeekerRepository;
import com.rev.app.repository.ResumeRepository;
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
public class ResumeServiceImplTest {

    @Mock
    private ResumeRepository resumeRepository;

    @Mock
    private JobSeekerRepository jobSeekerRepository;

    @Mock
    private JobSeekerMapper mapper;

    @InjectMocks
    private ResumeServiceImpl resumeService;

    @Test
    public void testCreateOrUpdateResume_Success() {
        ResumeDTO dto = new ResumeDTO();
        dto.setJobSeekerId(1L);
        dto.setObjective("N/A");

        JobSeeker seeker = new JobSeeker();
        seeker.setId(1L);

        when(jobSeekerRepository.findById(1L)).thenReturn(Optional.of(seeker));
        when(resumeRepository.findByJobSeeker(seeker)).thenReturn(Optional.empty());
        when(resumeRepository.save(any())).thenReturn(new Resume());
        when(mapper.toResumeDTO(any())).thenReturn(dto);

        ResumeDTO result = resumeService.createOrUpdateResume(dto);

        assertThat(result).isNotNull();
        verify(resumeRepository, times(1)).save(any());
    }

    @Test(expected = ResourceNotFoundException.class)
    public void testGetResumeBySeekerId_NotFound() {
        when(jobSeekerRepository.findById(1L)).thenReturn(Optional.of(new JobSeeker()));
        when(resumeRepository.findByJobSeeker(any())).thenReturn(Optional.empty());

        resumeService.getResumeBySeekerId(1L);
    }

    @Test
    public void testGetResumeBySeekerId_Success() {
        JobSeeker seeker = new JobSeeker();
        Resume resume = new Resume();
        when(jobSeekerRepository.findById(1L)).thenReturn(Optional.of(seeker));
        when(resumeRepository.findByJobSeeker(seeker)).thenReturn(Optional.of(resume));
        when(mapper.toResumeDTO(resume)).thenReturn(new ResumeDTO());

        ResumeDTO result = resumeService.getResumeBySeekerId(1L);

        assertThat(result).isNotNull();
    }

    @Test
    public void testDeleteResumeFile() {
        JobSeeker seeker = new JobSeeker();
        Resume resume = new Resume();
        resume.setFilePath("test.pdf");
        when(jobSeekerRepository.findById(1L)).thenReturn(Optional.of(seeker));
        when(resumeRepository.findByJobSeeker(seeker)).thenReturn(Optional.of(resume));

        resumeService.deleteResumeFile(1L);

        assertThat(resume.getFilePath()).isNull();
        verify(resumeRepository).save(resume);
    }
}
