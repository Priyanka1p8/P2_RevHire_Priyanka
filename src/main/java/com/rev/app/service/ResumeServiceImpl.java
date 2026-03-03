package com.rev.app.service;

import com.rev.app.dto.ResumeDTO;
import com.rev.app.entity.JobSeeker;
import com.rev.app.entity.Resume;
import com.rev.app.exception.ResourceNotFoundException;
import com.rev.app.mapper.AppMapper;
import com.rev.app.repository.JobSeekerRepository;
import com.rev.app.repository.ResumeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class ResumeServiceImpl implements ResumeService {

    private static final String UPLOAD_DIR = "uploads/resumes/";

    @Autowired
    private ResumeRepository resumeRepository;

    @Autowired
    private JobSeekerRepository jobSeekerRepository;

    @Autowired
    private AppMapper mapper;

    @Override
    public ResumeDTO createOrUpdateResume(ResumeDTO dto) {
        JobSeeker seeker = jobSeekerRepository.findById(dto.getJobSeekerId())
                .orElseThrow(() -> new ResourceNotFoundException("Job Seeker not found"));

        Resume resume = resumeRepository.findByJobSeeker(seeker).orElse(new Resume());
        resume.setJobSeeker(seeker);
        resume.setObjective(dto.getObjective());
        resume.setEducation(dto.getEducation());
        resume.setExperience(dto.getExperience());
        resume.setSkills(dto.getSkills());
        resume.setProjects(dto.getProjects());
        resume.setCertifications(dto.getCertifications());

        return mapper.toResumeDTO(resumeRepository.save(resume));
    }

    @Override
    public ResumeDTO getResumeBySeekerId(Long seekerId) {
        JobSeeker seeker = jobSeekerRepository.findById(seekerId)
                .orElseThrow(() -> new ResourceNotFoundException("Job Seeker not found"));
        Resume resume = resumeRepository.findByJobSeeker(seeker)
                .orElseThrow(() -> new ResourceNotFoundException("Resume not found"));
        return mapper.toResumeDTO(resume);
    }

    @Override
    public void uploadResumeFile(Long seekerId, MultipartFile file) throws IOException {
        JobSeeker seeker = jobSeekerRepository.findById(seekerId)
                .orElseThrow(() -> new ResourceNotFoundException("Job Seeker not found"));

        String originalName = file.getOriginalFilename();
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        Path filePath = uploadPath.resolve(seekerId + "_" + originalName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        Resume resume = resumeRepository.findByJobSeeker(seeker).orElse(new Resume());
        resume.setJobSeeker(seeker);
        resume.setFilePath(filePath.toString());
        resume.setFileName(originalName);
        resumeRepository.save(resume);
    }

    @Override
    public void deleteResumeFile(Long seekerId) {
        JobSeeker seeker = jobSeekerRepository.findById(seekerId)
                .orElseThrow(() -> new ResourceNotFoundException("Job Seeker not found"));
        Resume resume = resumeRepository.findByJobSeeker(seeker)
                .orElseThrow(() -> new ResourceNotFoundException("Resume not found"));

        if (resume.getFilePath() != null) {
            try {
                Files.deleteIfExists(Paths.get(resume.getFilePath()));
            } catch (IOException e) {
                // Log and continue, database record should still be cleared
            }
        }
        resume.setFilePath(null);
        resume.setFileName(null);
        resumeRepository.save(resume);
    }
}
