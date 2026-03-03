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
public class ApplicationNoteRepositoryTest {

    @Autowired
    private ApplicationNoteRepository applicationNoteRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private JobSeekerRepository jobSeekerRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmployerRepository employerRepository;

    @Autowired
    private ResumeRepository resumeRepository;

    @Autowired
    private CompanyRepository companyRepository;

    private Application application;
    private Employer employer;

    @Before
    public void setUp() {
        User seekerUser = new User();
        seekerUser.setEmail("noteseeker@revhire.com");
        seekerUser.setPassword("password");
        seekerUser.setRole(User.Role.JOB_SEEKER);
        userRepository.save(seekerUser);

        JobSeeker seeker = new JobSeeker();
        seeker.setUser(seekerUser);
        seeker.setName("Alice");
        jobSeekerRepository.save(seeker);

        Resume resume = new Resume();
        resume.setJobSeeker(seeker);
        resume.setSkills("Java");
        resumeRepository.save(resume);

        Company company = new Company();
        company.setName("Test Corp");
        company.setIndustry("IT");
        companyRepository.save(company);

        User employerUser = new User();
        employerUser.setEmail("noteemployer@revhire.com");
        employerUser.setPassword("password");
        employerUser.setRole(User.Role.EMPLOYER);
        userRepository.save(employerUser);

        employer = new Employer();
        employer.setUser(employerUser);
        employer.setCompany(company);
        employer.setContactPerson("Manager");
        employerRepository.save(employer);

        Job job = new Job();
        job.setTitle("Backend Dev");
        job.setSkillsRequired("Java");
        job.setLocation("Delhi");
        job.setSalaryRange(70000.0);
        job.setJobType("Full-time");
        job.setDeadline(LocalDate.now().plusDays(30));
        jobRepository.save(job);

        application = new Application();
        application.setJob(job);
        application.setJobSeeker(seeker);
        application.setResume(resume);
        application.setStatus(Application.ApplicationStatus.APPLIED);
        applicationRepository.save(application);
    }

    @Test
    public void testFindByApplication() {
        ApplicationNote note = new ApplicationNote();
        note.setApplication(application);
        note.setEmployer(employer);
        note.setNote("Good candidate");
        applicationNoteRepository.save(note);

        List<ApplicationNote> notes = applicationNoteRepository.findByApplication(application);
        assertThat(notes).hasSize(1);
        assertThat(notes.get(0).getNote()).isEqualTo("Good candidate");
    }

    @Test
    public void testSaveMultipleNotes() {
        ApplicationNote note1 = new ApplicationNote();
        note1.setApplication(application);
        note1.setEmployer(employer);
        note1.setNote("First impression");
        applicationNoteRepository.save(note1);

        ApplicationNote note2 = new ApplicationNote();
        note2.setApplication(application);
        note2.setEmployer(employer);
        note2.setNote("Second review");
        applicationNoteRepository.save(note2);

        List<ApplicationNote> notes = applicationNoteRepository.findByApplication(application);
        assertThat(notes).hasSize(2);
    }
}
