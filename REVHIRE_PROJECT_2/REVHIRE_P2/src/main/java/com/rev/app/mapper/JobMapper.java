package com.rev.app.mapper;

import com.rev.app.dto.CompanyDTO;
import com.rev.app.dto.EmployerDTO;
import com.rev.app.dto.JobDTO;
import com.rev.app.entity.Company;
import com.rev.app.entity.Employer;
import com.rev.app.entity.Job;
import org.springframework.stereotype.Component;

@Component
public class JobMapper {

    public JobDTO toJobDTO(Job job) {
        if (job == null)
            return null;
        JobDTO dto = new JobDTO();
        dto.setId(job.getId());
        dto.setTitle(job.getTitle());
        dto.setDescription(job.getDescription());
        dto.setSkillsRequired(job.getSkillsRequired());
        dto.setExperienceRequired(job.getExperienceRequired());
        dto.setEducationRequired(job.getEducationRequired());
        dto.setLocation(job.getLocation());
        dto.setSalaryRange(job.getSalaryRange());
        dto.setJobType(job.getJobType());
        dto.setDeadline(job.getDeadline());
        dto.setNumberOfOpenings(job.getNumberOfOpenings());
        dto.setIsClosed(job.getIsClosed());
        dto.setStatus(job.getStatus());
        dto.setEmployerId(job.getEmployer().getId());
        dto.setCompanyId(job.getCompany().getId());
        dto.setCompanyName(job.getCompany().getName());
        dto.setCompanyIndustry(job.getCompany().getIndustry());
        dto.setCompanyWebsite(job.getCompany().getWebsite());
        dto.setCompanySize(job.getCompany().getSize());
        dto.setCompanyDescription(job.getCompany().getDescription());
        dto.setPostedDate(job.getPostedDate());
        return dto;
    }

    public Job toJobEntity(JobDTO dto, Employer employer, Company company) {
        if (dto == null)
            return null;
        Job job = new Job();
        job.setId(dto.getId());
        job.setTitle(dto.getTitle());
        job.setDescription(dto.getDescription());
        job.setSkillsRequired(dto.getSkillsRequired());
        job.setExperienceRequired(dto.getExperienceRequired());
        job.setEducationRequired(dto.getEducationRequired());
        job.setLocation(dto.getLocation());
        job.setSalaryRange(dto.getSalaryRange());
        job.setJobType(dto.getJobType());
        job.setDeadline(dto.getDeadline());
        job.setNumberOfOpenings(dto.getNumberOfOpenings());
        job.setIsClosed(dto.getIsClosed() != null ? dto.getIsClosed() : false);
        job.setStatus(dto.getStatus());
        job.setEmployer(employer);
        job.setCompany(company);
        return job;
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

    public EmployerDTO toEmployerDTO(Employer employer) {
        if (employer == null)
            return null;
        EmployerDTO dto = new EmployerDTO();
        dto.setId(employer.getId());
        dto.setUserId(employer.getUser().getId());
        dto.setCompanyId(employer.getCompany().getId());
        dto.setContactPerson(employer.getContactPerson());
        dto.setDesignation(employer.getDesignation());
        dto.setEmail(employer.getUser().getEmail());
        return dto;
    }
}
