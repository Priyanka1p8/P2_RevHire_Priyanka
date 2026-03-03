package com.rev.app.service;

import com.rev.app.entity.User;
import com.rev.app.repository.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService userDetailsService;

    @Test
    public void testLoadUserByUsername_Success() {
        User user = new User();
        user.setEmail("test@test.com");
        user.setPassword("pwd");
        user.setRole(User.Role.JOB_SEEKER);

        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));

        UserDetails result = userDetailsService.loadUserByUsername("test@test.com");

        assertThat(result.getUsername()).isEqualTo("test@test.com");
        assertThat(result.getAuthorities()).hasSize(1);
    }

    @Test(expected = UsernameNotFoundException.class)
    public void testLoadUserByUsername_NotFound() {
        when(userRepository.findByEmail("non@test.com")).thenReturn(Optional.empty());
        userDetailsService.loadUserByUsername("non@test.com");
    }
}
