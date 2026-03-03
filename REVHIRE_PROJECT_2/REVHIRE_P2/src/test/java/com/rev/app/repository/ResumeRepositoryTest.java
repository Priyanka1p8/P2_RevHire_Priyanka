package com.rev.app.repository;

import com.rev.app.entity.JobSeeker;
import com.rev.app.entity.Resume;
import com.rev.app.entity.User;
import org.junit.Before;
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
public class ResumeRepositoryTest {

    @Autowired
    private ResumeRepository resumeRepository;

    @Autowired
    private JobSeekerRepository jobSeekerRepository;

    @Autowired
    private UserRepository userRepository;

    private JobSeeker seeker;

    @Before
    public void setUp() {
        User user = new User();
        user.setEmail("resume@revhire.com");
        user.setPassword("password");
        user.setRole(User.Role.JOB_SEEKER);
        userRepository.save(user);

        seeker = new JobSeeker();
        seeker.setUser(user);
        seeker.setName("Jane Doe");
        jobSeekerRepository.save(seeker);
    }

    @Test
    public void testFindByJobSeeker() {
        Resume resume = new Resume();
        resume.setJobSeeker(seeker);
        resume.setSkills("Java, Spring");
        resume.setEducation("B.Tech");
        resumeRepository.save(resume);

        Optional<Resume> found = resumeRepository.findByJobSeeker(seeker);
        assertThat(found).isPresent();
        assertThat(found.get().getSkills()).isEqualTo("Java, Spring");
    }

    @Test
    public void testFindByJobSeekerId() {
        Resume resume = new Resume();
        resume.setJobSeeker(seeker);
        resume.setSkills("Python, ML");
        resumeRepository.save(resume);

        Optional<Resume> found = resumeRepository.findByJobSeekerId(seeker.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getSkills()).isEqualTo("Python, ML");
    }
}
