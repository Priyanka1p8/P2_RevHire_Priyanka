package com.rev.app.mapper;

import com.rev.app.dto.CompanyDTO;
import com.rev.app.dto.EmployerDTO;
import com.rev.app.entity.Company;
import com.rev.app.entity.Employer;
import org.springframework.stereotype.Component;

@Component
public class EmployerMapper {

    public EmployerDTO toEmployerDTO(Employer employer) {
        if (employer == null)
            return null;
        EmployerDTO dto = new EmployerDTO();
        dto.setId(employer.getId());
        dto.setUserId(employer.getUser().getId());
        dto.setCompanyId(employer.getCompany() != null ? employer.getCompany().getId() : null);
        dto.setContactPerson(employer.getContactPerson());
        dto.setDesignation(employer.getDesignation());
        dto.setEmail(employer.getUser().getEmail());
        if (employer.getCompany() != null) {
            dto.setCompany(toCompanyDTO(employer.getCompany()));
        }
        return dto;
    }

    public CompanyDTO toCompanyDTO(Company company) {
        if (company == null)
            return null;
        CompanyDTO dto = new CompanyDTO();
        dto.setId(company.getId());
        dto.setName(company.getName());
        dto.setIndustry(company.getIndustry());
        dto.setSize(company.getSize());
        dto.setDescription(company.getDescription());
        dto.setWebsite(company.getWebsite());
        dto.setLocation(company.getLocation());
        return dto;
    }
}
