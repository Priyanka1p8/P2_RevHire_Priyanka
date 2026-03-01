package com.rev.app.mapper;

import com.rev.app.dto.*;
import com.rev.app.entity.*;
import org.springframework.stereotype.Component;

@Component
public class AppMapper {

    public JobDTO toJobDTO(Job job) {
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

    public ApplicationDTO toApplicationDTO(Application app) {
        ApplicationDTO dto = new ApplicationDTO();
        dto.setId(app.getId());
        dto.setJobId(app.getJob().getId());
        dto.setJobTitle(app.getJob().getTitle());
        dto.setCompanyName(app.getJob().getCompany().getName());
        dto.setJobSeekerId(app.getJobSeeker().getId());
        dto.setSeekerName(app.getJobSeeker().getName());
        dto.setResumeId(app.getResume().getId());
        dto.setCoverLetter(app.getCoverLetter());
        dto.setStatus(app.getStatus());
        dto.setAppliedDate(app.getAppliedDate());
        dto.setWithdrawReason(app.getWithdrawReason());
        if (app.getApplicationNotes() != null) {
            dto.setNotes(app.getApplicationNotes().stream()
                    .map(this::toNoteDTO)
                    .collect(java.util.stream.Collectors.toList()));
        }
        return dto;
    }

    public NoteDTO toNoteDTO(ApplicationNote note) {
        NoteDTO dto = new NoteDTO();
        dto.setId(note.getId());
        dto.setNote(note.getNote());
        dto.setCreatedAt(note.getCreatedAt());
        return dto;
    }

    public ResumeDTO toResumeDTO(Resume resume) {
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

    public SavedJobDTO toSavedJobDTO(SavedJob savedJob) {
        SavedJobDTO dto = new SavedJobDTO();
        dto.setId(savedJob.getId());
        dto.setJobSeekerId(savedJob.getJobSeeker().getId());
        dto.setJobId(savedJob.getJob().getId());
        dto.setJobTitle(savedJob.getJob().getTitle());
        dto.setCompanyName(savedJob.getJob().getCompany().getName());
        dto.setLocation(savedJob.getJob().getLocation());
        dto.setSalaryRange(savedJob.getJob().getSalaryRange());
        dto.setSavedAt(savedJob.getSavedAt());
        return dto;
    }
}
