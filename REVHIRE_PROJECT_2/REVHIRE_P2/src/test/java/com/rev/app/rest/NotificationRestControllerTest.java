package com.rev.app.rest;

import com.rev.app.config.JwtUtil;
import com.rev.app.dto.NotificationDTO;
import com.rev.app.service.NotificationService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@RunWith(SpringRunner.class)
@WebMvcTest(NotificationRestController.class)
@AutoConfigureMockMvc(addFilters = false)
public class NotificationRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NotificationService notificationService;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private UserDetailsService userDetailsService;

    @MockBean
    private JwtUtil jwtUtil;

    @Test
    public void testGetNotifications() throws Exception {
        Long userId = 1L;
        NotificationDTO dto = new NotificationDTO();
        dto.setMessage("Test Notification");
        List<NotificationDTO> notifications = Arrays.asList(dto);

        when(notificationService.getNotificationsForUser(userId)).thenReturn(notifications);

        mockMvc.perform(get("/api/notifications/user/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].message").value("Test Notification"));
    }

    @Test
    public void testGetUnreadCount() throws Exception {
        Long userId = 1L;
        when(notificationService.getUnreadCount(userId)).thenReturn(5L);

        mockMvc.perform(get("/api/notifications/user/{userId}/unread-count", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(5));
    }

    @Test
    public void testMarkAsRead() throws Exception {
        Long notificationId = 1L;
        doNothing().when(notificationService).markAsRead(notificationId);

        mockMvc.perform(put("/api/notifications/{id}/read", notificationId))
                .andExpect(status().isOk());

        verify(notificationService, times(1)).markAsRead(notificationId);
    }

    @Test
    public void testMarkAllAsRead() throws Exception {
        Long userId = 1L;
        doNothing().when(notificationService).markAllRead(userId);

        mockMvc.perform(put("/api/notifications/user/{userId}/read-all", userId))
                .andExpect(status().isOk());

        verify(notificationService, times(1)).markAllRead(userId);
    }
}
