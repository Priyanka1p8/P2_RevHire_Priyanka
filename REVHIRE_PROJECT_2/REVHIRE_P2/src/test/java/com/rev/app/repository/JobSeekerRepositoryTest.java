package com.rev.app.repository;

import com.rev.app.entity.JobSeeker;
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
public class JobSeekerRepositoryTest {

    @Autowired
    private JobSeekerRepository jobSeekerRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testFindByUser() {
        User user = new User();
        user.setEmail("seeker_repo@revhire.com");
        user.setPassword("password");
        user.setRole(User.Role.JOB_SEEKER);
        userRepository.save(user);

        JobSeeker seeker = new JobSeeker();
        seeker.setUser(user);
        seeker.setName("Seeker A");
        jobSeekerRepository.save(seeker);

        Optional<JobSeeker> found = jobSeekerRepository.findByUser(user);
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Seeker A");
    }
}
