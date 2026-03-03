package com.rev.app.repository;

import com.rev.app.entity.Notification;
import com.rev.app.entity.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
@ActiveProfiles("test")
public class NotificationRepositoryTest {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    private User user;

    @Before
    public void setUp() {
        user = new User();
        user.setEmail("notify@revhire.com");
        user.setPassword("password");
        user.setRole(User.Role.JOB_SEEKER);
        userRepository.save(user);
    }

    @Test
    public void testFindByUserOrderByCreatedAtDesc() {
        Notification n1 = new Notification();
        n1.setUser(user);
        n1.setMessage("First notification");
        n1.setIsRead(false);
        notificationRepository.save(n1);

        Notification n2 = new Notification();
        n2.setUser(user);
        n2.setMessage("Second notification");
        n2.setIsRead(true);
        notificationRepository.save(n2);

        List<Notification> notifications = notificationRepository.findByUserOrderByCreatedAtDesc(user);
        assertThat(notifications).hasSize(2);
    }

    @Test
    public void testCountByUserAndIsReadFalse() {
        Notification n1 = new Notification();
        n1.setUser(user);
        n1.setMessage("Unread notification");
        n1.setIsRead(false);
        notificationRepository.save(n1);

        Notification n2 = new Notification();
        n2.setUser(user);
        n2.setMessage("Read notification");
        n2.setIsRead(true);
        notificationRepository.save(n2);

        long unreadCount = notificationRepository.countByUserAndIsReadFalse(user);
        assertThat(unreadCount).isEqualTo(1);
    }
}
