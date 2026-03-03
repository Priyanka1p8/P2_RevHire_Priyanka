package com.rev.app.repository;

import com.rev.app.entity.Company;
import com.rev.app.entity.Employer;
import com.rev.app.entity.Job;
import com.rev.app.entity.User;
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
public class JobRepositoryTest {

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private EmployerRepository employerRepository;

    @Autowired
    private UserRepository userRepository;

    private Employer employer;
    private Company company;

    @Before
    public void setUp() {
        User user = new User();
        user.setEmail("employer@revhire.com");
        user.setPassword("password");
        user.setRole(User.Role.EMPLOYER);
        userRepository.save(user);

        company = new Company();
        company.setName("Revature");
        company.setIndustry("IT");
        companyRepository.save(company);

        employer = new Employer();
        employer.setUser(user);
        employer.setCompany(company);
        employerRepository.save(employer);
    }

    @Test
    public void testSaveAndFindByEmployer() {
        Job job = new Job();
        job.setTitle("Software Engineer");
        job.setDescription("Great role");
        job.setSkillsRequired("Java, Spring");
        job.setLocation("Chennai");
        job.setSalaryRange(50000.0);
        job.setJobType("Full-time");
        job.setDeadline(LocalDate.now().plusDays(30));
        job.setEmployer(employer);
        job.setCompany(company);
        jobRepository.save(job);

        List<Job> jobs = jobRepository.findByEmployer(employer);
        assertThat(jobs).isNotEmpty();
        assertThat(jobs.get(0).getTitle()).isEqualTo("Software Engineer");
    }

    @Test
    public void testSearchJobsAdvanced() {
        Job job = new Job();
        job.setTitle("Java Developer");
        job.setSkillsRequired("Java");
        job.setLocation("Pune");
        job.setSalaryRange(60000.0);
        job.setJobType("Full-time");
        job.setDeadline(LocalDate.now().plusDays(30));
        job.setEmployer(employer);
        job.setCompany(company);
        jobRepository.save(job);

        List<Job> results = jobRepository.searchJobsAdvanced("%java%", null, "Full-time", null, 50000.0, null);
        assertThat(results).isNotEmpty();
        assertThat(results.get(0).getTitle()).isEqualTo("Java Developer");
    }
}
