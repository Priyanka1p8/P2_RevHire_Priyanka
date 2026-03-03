package com.rev.app.service;

import com.rev.app.dto.UserDTO;
import com.rev.app.entity.User;
import com.rev.app.repository.CompanyRepository;
import com.rev.app.repository.EmployerRepository;
import com.rev.app.repository.JobSeekerRepository;
import com.rev.app.repository.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JobSeekerRepository jobSeekerRepository;

    @Mock
    private EmployerRepository employerRepository;

    @Mock
    private CompanyRepository companyRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    public void testRegisterUser_JobSeeker() {
        UserDTO dto = new UserDTO();
        dto.setEmail("test@test.com");
        dto.setPassword("pwd");
        dto.setRole(User.Role.JOB_SEEKER);
        dto.setName("John");

        User user = new User();
        user.setEmail(dto.getEmail());
        user.setRole(dto.getRole());

        when(passwordEncoder.encode(anyString())).thenReturn("encodedPwd");
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = userService.registerUser(dto);

        assertThat(result.getEmail()).isEqualTo(dto.getEmail());
        verify(jobSeekerRepository, times(1)).save(any());
        verify(employerRepository, never()).save(any());
    }

    @Test
    public void testRegisterUser_Employer() {
        UserDTO dto = new UserDTO();
        dto.setEmail("emp@test.com");
        dto.setPassword("pwd");
        dto.setRole(User.Role.EMPLOYER);
        dto.setCompanyName("Tech Corp");

        User user = new User();
        user.setEmail(dto.getEmail());
        user.setRole(dto.getRole());

        when(passwordEncoder.encode(anyString())).thenReturn("encodedPwd");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(companyRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        User result = userService.registerUser(dto);

        assertThat(result.getEmail()).isEqualTo(dto.getEmail());
        verify(employerRepository, times(1)).save(any());
        verify(jobSeekerRepository, never()).save(any());
    }

    @Test
    public void testFindByEmail() {
        when(userRepository.findByEmail("a@b.com")).thenReturn(Optional.of(new User()));
        assertThat(userService.findByEmail("a@b.com")).isPresent();
    }

    @Test
    public void testExistsByEmail() {
        when(userRepository.existsByEmail("test@test.com")).thenReturn(true);
        boolean exists = userService.existsByEmail("test@test.com");
        assertThat(exists).isTrue();
    }
}
