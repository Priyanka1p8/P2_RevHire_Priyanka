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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
@ActiveProfiles("test")
public class SavedJobRepositoryTest {

    @Autowired
    private SavedJobRepository savedJobRepository;

    @Autowired
    private JobSeekerRepository jobSeekerRepository;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private UserRepository userRepository;

    private JobSeeker seeker;
    private Job job;

    @Before
    public void setUp() {
        User user = new User();
        user.setEmail("saved@revhire.com");
        user.setPassword("password");
        user.setRole(User.Role.JOB_SEEKER);
        userRepository.save(user);

        seeker = new JobSeeker();
        seeker.setUser(user);
        seeker.setName("Bob Smith");
        jobSeekerRepository.save(seeker);

        job = new Job();
        job.setTitle("Java Developer");
        job.setSkillsRequired("Java");
        job.setLocation("Hyderabad");
        job.setSalaryRange(60000.0);
        job.setJobType("Full-time");
        job.setDeadline(LocalDate.now().plusDays(30));
        jobRepository.save(job);
    }

    @Test
    public void testFindByJobSeeker() {
        SavedJob savedJob = new SavedJob();
        savedJob.setJobSeeker(seeker);
        savedJob.setJob(job);
        savedJobRepository.save(savedJob);

        List<SavedJob> savedJobs = savedJobRepository.findByJobSeeker(seeker);
        assertThat(savedJobs).hasSize(1);
        assertThat(savedJobs.get(0).getJob().getTitle()).isEqualTo("Java Developer");
    }

    @Test
    public void testFindByJobSeekerAndJob() {
        SavedJob savedJob = new SavedJob();
        savedJob.setJobSeeker(seeker);
        savedJob.setJob(job);
        savedJobRepository.save(savedJob);

        Optional<SavedJob> found = savedJobRepository.findByJobSeekerAndJob(seeker, job);
        assertThat(found).isPresent();
    }

    @Test
    public void testExistsByJobSeekerAndJob() {
        SavedJob savedJob = new SavedJob();
        savedJob.setJobSeeker(seeker);
        savedJob.setJob(job);
        savedJobRepository.save(savedJob);

        boolean exists = savedJobRepository.existsByJobSeekerAndJob(seeker, job);
        assertThat(exists).isTrue();
    }
}
