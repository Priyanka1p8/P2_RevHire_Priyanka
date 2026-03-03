package com.rev.app.service;

import com.rev.app.dto.NotificationDTO;
import com.rev.app.entity.Notification;
import com.rev.app.entity.User;
import com.rev.app.exception.ResourceNotFoundException;
import com.rev.app.mapper.NotificationMapper;
import com.rev.app.repository.JobSeekerRepository;
import com.rev.app.repository.NotificationRepository;
import com.rev.app.repository.UserRepository;
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
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class NotificationServiceImplTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JobSeekerRepository jobSeekerRepository;

    @Mock
    private NotificationMapper mapper;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    @Test
    public void testSendNotification_Success() {
        User user = new User();
        user.setId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        notificationService.sendNotification(1L, "Hello");

        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    @Test(expected = ResourceNotFoundException.class)
    public void testSendNotification_UserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        notificationService.sendNotification(1L, "Hello");
    }

    @Test
    public void testSendNotificationToSeeker() {
        com.rev.app.entity.JobSeeker seeker = new com.rev.app.entity.JobSeeker();
        User user = new User();
        user.setId(1L);
        seeker.setUser(user);
        when(jobSeekerRepository.findById(1L)).thenReturn(Optional.of(seeker));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        notificationService.sendNotificationToSeeker(1L, "Seeker Msg");

        verify(notificationRepository).save(any(Notification.class));
    }

    @Test
    public void testGetNotificationsForUser() {
        User user = new User();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(notificationRepository.findByUserOrderByCreatedAtDesc(user)).thenReturn(Collections.emptyList());

        List<NotificationDTO> result = notificationService.getNotificationsForUser(1L);

        assertThat(result).isEmpty();
    }

    @Test
    public void testMarkAsRead() {
        Notification n = new Notification();
        n.setId(1L);
        n.setIsRead(false);
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(n));

        notificationService.markAsRead(1L);

        assertThat(n.getIsRead()).isTrue();
        verify(notificationRepository, times(1)).save(n);
    }

    @Test
    public void testMarkAllRead() {
        User user = new User();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(notificationRepository.findByUserOrderByCreatedAtDesc(user)).thenReturn(Collections.emptyList());

        notificationService.markAllRead(1L);

        verify(notificationRepository).saveAll(anyList());
    }

    @Test
    public void testGetUnreadCount() {
        User user = new User();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(notificationRepository.countByUserAndIsReadFalse(user)).thenReturn(5L);

        long count = notificationService.getUnreadCount(1L);

        assertThat(count).isEqualTo(5L);
    }
}
