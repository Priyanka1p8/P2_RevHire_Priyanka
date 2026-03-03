package com.rev.app.service;

import com.rev.app.dto.UserDTO;
import com.rev.app.entity.User;
import com.rev.app.entity.JobSeeker;
import com.rev.app.entity.Employer;
import com.rev.app.entity.Company;
import com.rev.app.repository.JobSeekerRepository;
import com.rev.app.repository.EmployerRepository;
import com.rev.app.repository.CompanyRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private static final Logger logger = LogManager.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JobSeekerRepository jobSeekerRepository;
    private final EmployerRepository employerRepository;
    private final CompanyRepository companyRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           JobSeekerRepository jobSeekerRepository,
                           EmployerRepository employerRepository,
                           CompanyRepository companyRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jobSeekerRepository = jobSeekerRepository;
        this.employerRepository = employerRepository;
        this.companyRepository = companyRepository;
    }

    @Override
    public User registerUser(UserDTO userDTO) {
        User user = new User();
        user.setEmail(userDTO.getEmail());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setRole(userDTO.getRole());
        logger.info("Registering new user with email: {} and role: {}", userDTO.getEmail(), userDTO.getRole());
        user = userRepository.save(user);

        if (user.getRole() == User.Role.JOB_SEEKER) {
            JobSeeker seeker = new JobSeeker();
            seeker.setUser(user);
            seeker.setName(userDTO.getName() != null ? userDTO.getName() : "New Seeker");
            seeker.setPhone(userDTO.getPhone());
            seeker.setLocation(userDTO.getLocation());
            seeker.setEmploymentStatus(userDTO.getEmploymentStatus());
            jobSeekerRepository.save(seeker);
            logger.info("JobSeeker profile created for user: {}", user.getEmail());
        } else if (user.getRole() == User.Role.EMPLOYER) {
            Company company = new Company();
            company.setName(userDTO.getCompanyName() != null ? userDTO.getCompanyName() : "New Company");
            company.setIndustry(userDTO.getIndustry());
            company.setSize(userDTO.getCompanySize());
            company.setDescription(userDTO.getDescription());
            company.setWebsite(userDTO.getWebsite());
            company.setLocation(userDTO.getLocation());
            company = companyRepository.save(company);

            Employer employer = new Employer();
            employer.setUser(user);
            employer.setCompany(company);
            employer.setContactPerson(userDTO.getName());
            employerRepository.save(employer);
            logger.info("Employer profile created for user: {} with company: {}", user.getEmail(), company.getName());
        }
        return user;
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}
