package com.rev.app.service;

import com.rev.app.dto.EmployerDTO;
import com.rev.app.entity.Company;
import com.rev.app.entity.Employer;
import com.rev.app.exception.ResourceNotFoundException;
import com.rev.app.mapper.EmployerMapper;
import com.rev.app.repository.CompanyRepository;
import com.rev.app.repository.EmployerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@Transactional
public class EmployerServiceImpl implements EmployerService {

    @Autowired
    private EmployerRepository employerRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private EmployerMapper mapper;

    @Autowired
    private JobService jobService;

    @Override
    public EmployerDTO getProfileByUserId(Long userId) {
        Employer employer = employerRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Employer profile not found"));
        return mapper.toEmployerDTO(employer);
    }

    @Override
    public EmployerDTO getProfileById(Long id) {
        Employer employer = employerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employer not found"));
        return mapper.toEmployerDTO(employer);
    }

    @Override
    public EmployerDTO updateProfile(Long id, EmployerDTO dto) {
        Employer employer = employerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employer not found"));

        employer.setContactPerson(dto.getContactPerson());
        employer.setDesignation(dto.getDesignation());

        if (dto.getCompany() != null) {
            Company company = employer.getCompany();
            if (company == null) {
                company = new Company();
                employer.setCompany(company);
            }
            company.setName(dto.getCompany().getName());
            company.setIndustry(dto.getCompany().getIndustry());
            company.setSize(dto.getCompany().getSize());
            company.setDescription(dto.getCompany().getDescription());
            company.setWebsite(dto.getCompany().getWebsite());
            company.setLocation(dto.getCompany().getLocation());
            companyRepository.save(company);
        }

        return mapper.toEmployerDTO(employerRepository.save(employer));
    }

    @Override
    public Map<String, Object> getStatistics(Long employerId) {
        Map<String, Object> stats = new HashMap<>();
        long totalJobs = jobService.getJobsByEmployer(employerId).size();
        long activeJobs = jobService.getJobsByEmployer(employerId).stream().filter(j -> !j.getIsClosed()).count();

        stats.put("totalJobs", totalJobs);
        stats.put("activeJobs", activeJobs);

        return stats;
    }
}
