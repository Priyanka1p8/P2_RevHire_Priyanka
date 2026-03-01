package com.rev.app.service;

import com.rev.app.dto.NotificationDTO;
import java.util.List;

public interface NotificationService {
    void sendNotification(Long userId, String message);

    void sendNotificationToSeeker(Long seekerId, String message);

    List<NotificationDTO> getNotificationsForUser(Long userId);

    void markAsRead(Long notificationId);

    void markAllRead(Long userId);

    long getUnreadCount(Long userId);
}
