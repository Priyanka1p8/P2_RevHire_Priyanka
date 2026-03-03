package com.rev.app.repository;

import com.rev.app.entity.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
@ActiveProfiles("test")
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testSaveAndFindByEmail() {
        // Arrange
        User user = new User();
        user.setEmail("test@revhire.com");
        user.setPassword("password123");
        user.setRole(User.Role.JOB_SEEKER);
        userRepository.save(user);

        // Act
        Optional<User> foundUser = userRepository.findByEmail("test@revhire.com");

        // Assert
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getEmail()).isEqualTo("test@revhire.com");
    }

    @Test
    public void testExistsByEmail() {
        // Arrange
        User user = new User();
        user.setEmail("exists@revhire.com");
        user.setPassword("password123");
        user.setRole(User.Role.EMPLOYER);
        userRepository.save(user);

        // Act & Assert
        assertThat(userRepository.existsByEmail("exists@revhire.com")).isTrue();
        assertThat(userRepository.existsByEmail("nonexistent@revhire.com")).isFalse();
    }
}
