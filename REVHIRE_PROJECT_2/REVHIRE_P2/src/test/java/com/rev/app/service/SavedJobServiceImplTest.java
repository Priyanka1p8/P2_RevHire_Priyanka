package com.rev.app.service;

import com.rev.app.entity.Job;
import com.rev.app.entity.JobSeeker;
import com.rev.app.entity.SavedJob;
import com.rev.app.mapper.ApplicationMapper;
import com.rev.app.repository.JobRepository;
import com.rev.app.repository.JobSeekerRepository;
import com.rev.app.repository.SavedJobRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SavedJobServiceImplTest {

    @Mock
    private SavedJobRepository savedJobRepository;

    @Mock
    private JobRepository jobRepository;

    @Mock
    private JobSeekerRepository jobSeekerRepository;

    @Mock
    private ApplicationMapper mapper;

    @InjectMocks
    private SavedJobServiceImpl savedJobService;

    @Test
    public void testSaveJob_Success() {
        JobSeeker seeker = new JobSeeker();
        Job job = new Job();

        when(jobSeekerRepository.findById(1L)).thenReturn(Optional.of(seeker));
        when(jobRepository.findById(1L)).thenReturn(Optional.of(job));
        when(savedJobRepository.existsByJobSeekerAndJob(seeker, job)).thenReturn(false);

        savedJobService.saveJob(1L, 1L);

        verify(savedJobRepository, times(1)).save(any(SavedJob.class));
    }

    @Test
    public void testUnsaveJob() {
        JobSeeker seeker = new JobSeeker();
        Job job = new Job();
        SavedJob savedJob = new SavedJob();

        when(jobSeekerRepository.findById(1L)).thenReturn(Optional.of(seeker));
        when(jobRepository.findById(1L)).thenReturn(Optional.of(job));
        when(savedJobRepository.findByJobSeekerAndJob(seeker, job)).thenReturn(Optional.of(savedJob));

        savedJobService.unsaveJob(1L, 1L);

        verify(savedJobRepository, times(1)).delete(savedJob);
    }

    @Test
    public void testGetSavedJobsBySeeker() {
        JobSeeker seeker = new JobSeeker();
        when(jobSeekerRepository.findById(1L)).thenReturn(Optional.of(seeker));
        when(savedJobRepository.findByJobSeeker(seeker)).thenReturn(java.util.Collections.emptyList());

        List<com.rev.app.dto.SavedJobDTO> result = savedJobService.getSavedJobsBySeeker(1L);

        assertThat(result).isEmpty();
    }

    @Test
    public void testIsJobSaved() {
        JobSeeker seeker = new JobSeeker();
        Job job = new Job();
        when(jobSeekerRepository.findById(1L)).thenReturn(Optional.of(seeker));
        when(jobRepository.findById(1L)).thenReturn(Optional.of(job));
        when(savedJobRepository.existsByJobSeekerAndJob(seeker, job)).thenReturn(true);

        boolean result = savedJobService.isJobSaved(1L, 1L);

        assertThat(result).isTrue();
    }
}
