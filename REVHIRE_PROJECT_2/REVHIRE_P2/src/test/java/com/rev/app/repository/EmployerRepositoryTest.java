package com.rev.app.repository;

import com.rev.app.entity.Company;
import com.rev.app.entity.Employer;
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
public class EmployerRepositoryTest {

    @Autowired
    private EmployerRepository employerRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Test
    public void testFindByUser() {
        User user = new User();
        user.setEmail("employer_repo@revhire.com");
        user.setPassword("password");
        user.setRole(User.Role.EMPLOYER);
        userRepository.save(user);

        Company company = new Company();
        company.setName("Test Co");
        company.setIndustry("Logic");
        companyRepository.save(company);

        Employer employer = new Employer();
        employer.setUser(user);
        employer.setCompany(company);
        employer.setContactPerson("Person A");
        employerRepository.save(employer);

        Optional<Employer> found = employerRepository.findByUser(user);
        assertThat(found).isPresent();
        assertThat(found.get().getContactPerson()).isEqualTo("Person A");
    }
}
