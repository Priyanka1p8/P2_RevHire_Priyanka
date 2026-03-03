package com.rev.app.mapper;

import com.rev.app.dto.JobSeekerDTO;
import com.rev.app.dto.ResumeDTO;
import com.rev.app.entity.JobSeeker;
import com.rev.app.entity.Resume;
import org.springframework.stereotype.Component;

@Component
public class JobSeekerMapper {

    public JobSeekerDTO toJobSeekerDTO(JobSeeker seeker) {
        if (seeker == null)
            return null;
        JobSeekerDTO dto = new JobSeekerDTO();
        dto.setId(seeker.getId());
        dto.setUserId(seeker.getUser().getId());
        dto.setName(seeker.getName());
        dto.setPhone(seeker.getPhone());
        dto.setLocation(seeker.getLocation());
        dto.setEmploymentStatus(seeker.getEmploymentStatus());
        dto.setEmail(seeker.getUser().getEmail());
        return dto;
    }

    public ResumeDTO toResumeDTO(Resume resume) {
        if (resume == null)
            return null;
        ResumeDTO dto = new ResumeDTO();
        dto.setId(resume.getId());
        dto.setJobSeekerId(resume.getJobSeeker().getId());
        dto.setObjective(resume.getObjective());
        dto.setEducation(resume.getEducation());
        dto.setExperience(resume.getExperience());
        dto.setSkills(resume.getSkills());
        dto.setProjects(resume.getProjects());
        dto.setCertifications(resume.getCertifications());
        dto.setFilePath(resume.getFilePath());
        dto.setFileName(resume.getFileName());
        dto.setSeekerName(resume.getJobSeeker().getName());
        dto.setSeekerEmail(resume.getJobSeeker().getUser().getEmail());
        dto.setSeekerPhone(resume.getJobSeeker().getPhone());
        dto.setSeekerLocation(resume.getJobSeeker().getLocation());
        dto.setSeekerEmploymentStatus(resume.getJobSeeker().getEmploymentStatus());
        return dto;
    }
}
