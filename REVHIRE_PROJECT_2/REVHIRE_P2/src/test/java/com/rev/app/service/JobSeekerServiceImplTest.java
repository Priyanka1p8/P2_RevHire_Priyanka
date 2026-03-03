package com.rev.app.service;

import com.rev.app.dto.JobSeekerDTO;
import com.rev.app.entity.JobSeeker;
import com.rev.app.exception.ResourceNotFoundException;
import com.rev.app.mapper.JobSeekerMapper;
import com.rev.app.repository.JobSeekerRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.rev.app.entity.User;
import java.util.Optional;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class JobSeekerServiceImplTest {

    @Mock
    private JobSeekerRepository jobSeekerRepository;

    @Mock
    private JobSeekerMapper mapper;

    @Mock
    private ApplicationService applicationService;

    @Mock
    private SavedJobService savedJobService;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private JobSeekerServiceImpl jobSeekerService;

    @Test
    public void testGetProfileByUserId_Success() {
        JobSeeker seeker = new JobSeeker();
        seeker.setId(1L);
        when(jobSeekerRepository.findByUserId(anyLong())).thenReturn(Optional.of(seeker));
        when(mapper.toJobSeekerDTO(seeker)).thenReturn(new JobSeekerDTO());

        JobSeekerDTO result = jobSeekerService.getProfileByUserId(1L);

        assertThat(result).isNotNull();
    }

    @Test(expected = ResourceNotFoundException.class)
    public void testGetProfileByUserId_NotFound() {
        when(jobSeekerRepository.findByUserId(anyLong())).thenReturn(Optional.empty());
        jobSeekerService.getProfileByUserId(1L);
    }

    @Test
    public void testGetProfileById_Success() {
        JobSeeker seeker = new JobSeeker();
        seeker.setId(1L);
        when(jobSeekerRepository.findById(1L)).thenReturn(Optional.of(seeker));
        when(mapper.toJobSeekerDTO(seeker)).thenReturn(new JobSeekerDTO());

        JobSeekerDTO result = jobSeekerService.getProfileById(1L);

        assertThat(result).isNotNull();
    }

    @Test
    public void testUpdateProfile() {
        JobSeeker seeker = new JobSeeker();
        seeker.setId(1L);
        JobSeekerDTO dto = new JobSeekerDTO();
        dto.setName("Updated Name");

        when(jobSeekerRepository.findById(1L)).thenReturn(Optional.of(seeker));
        when(jobSeekerRepository.save(any(JobSeeker.class))).thenReturn(seeker);
        when(mapper.toJobSeekerDTO(any())).thenReturn(dto);

        JobSeekerDTO result = jobSeekerService.updateProfile(1L, dto);

        assertThat(result.getName()).isEqualTo("Updated Name");
        verify(jobSeekerRepository).save(seeker);
    }

    @Test
    public void testGetDashboardSummary() {
        Long seekerId = 1L;
        JobSeeker seeker = new JobSeeker();
        User user = new User();
        user.setId(1L);
        seeker.setUser(user);

        when(applicationService.getApplicationsBySeeker(seekerId)).thenReturn(java.util.Collections.emptyList());
        when(savedJobService.getSavedJobsBySeeker(seekerId)).thenReturn(java.util.Collections.emptyList());
        when(jobSeekerRepository.findById(seekerId)).thenReturn(Optional.of(seeker));
        when(notificationService.getUnreadCount(1L)).thenReturn(5L);

        Map<String, Object> summary = jobSeekerService.getDashboardSummary(seekerId);

        assertThat(summary).containsEntry("applicationCount", 0);
        assertThat(summary).containsEntry("savedJobCount", 0);
        assertThat(summary).containsEntry("unreadNotificationCount", 5L);
    }
}
