package com.rev.app.repository;

import com.rev.app.entity.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
@ActiveProfiles("test")
public class ApplicationRepositoryTest {

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private JobSeekerRepository jobSeekerRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ResumeRepository resumeRepository;

    private Job job;
    private JobSeeker seeker;
    private Resume resume;

    @Before
    public void setUp() {
        User seekerUser = new User();
        seekerUser.setEmail("seeker@revhire.com");
        seekerUser.setPassword("password");
        seekerUser.setRole(User.Role.JOB_SEEKER);
        userRepository.save(seekerUser);

        seeker = new JobSeeker();
        seeker.setUser(seekerUser);
        seeker.setName("John Doe");
        jobSeekerRepository.save(seeker);

        resume = new Resume();
        resume.setJobSeeker(seeker);
        resume.setSkills("Java, SQL");
        resumeRepository.save(resume);

        job = new Job();
        job.setTitle("Software Engineer");
        job.setSkillsRequired("Java");
        job.setLocation("Chennai");
        job.setSalaryRange(50000.0);
        job.setJobType("Full-time");
        job.setDeadline(LocalDate.now().plusDays(30));
        jobRepository.save(job);
    }

    @Test
    public void testSaveAndExistsByJobSeekerAndJob() {
        Application app = new Application();
        app.setJob(job);
        app.setJobSeeker(seeker);
        app.setResume(resume);
        app.setStatus(Application.ApplicationStatus.APPLIED);
        applicationRepository.save(app);

        boolean exists = applicationRepository.existsByJobSeekerAndJob(seeker, job);
        assertThat(exists).isTrue();
    }

    @Test
    public void testFindByJobSeeker() {
        Application app = new Application();
        app.setJob(job);
        app.setJobSeeker(seeker);
        app.setResume(resume);
        applicationRepository.save(app);

        List<Application> apps = applicationRepository.findByJobSeeker(seeker);
        assertThat(apps).hasSize(1);
    }
}
