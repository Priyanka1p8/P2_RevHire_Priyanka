package com.rev.app.repository;

import com.rev.app.entity.Company;
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
public class CompanyRepositoryTest {

    @Autowired
    private CompanyRepository companyRepository;

    @Test
    public void testSaveAndFindById() {
        Company company = new Company();
        company.setName("Revature");
        company.setIndustry("IT Services");
        company.setSize("1000+");
        company.setLocation("Reston, VA");

        Company saved = companyRepository.save(company);

        Optional<Company> found = companyRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Revature");
        assertThat(found.get().getIndustry()).isEqualTo("IT Services");
    }

    @Test
    public void testFindAll() {
        Company c1 = new Company();
        c1.setName("Company A");
        c1.setIndustry("Tech");
        companyRepository.save(c1);

        Company c2 = new Company();
        c2.setName("Company B");
        c2.setIndustry("Finance");
        companyRepository.save(c2);

        assertThat(companyRepository.findAll()).hasSize(2);
    }
}
