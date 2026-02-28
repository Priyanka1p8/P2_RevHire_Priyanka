package com.rev.app.service;

import com.rev.app.dto.JobDTO;
import com.rev.app.entity.Company;
import com.rev.app.entity.Employer;
import com.rev.app.entity.Job;
import com.rev.app.exception.ResourceNotFoundException;
import com.rev.app.mapper.AppMapper;
import com.rev.app.repository.ApplicationRepository;
import com.rev.app.repository.CompanyRepository;
import com.rev.app.repository.EmployerRepository;
import com.rev.app.repository.JobRepository;
import com.rev.app.repository.JobSeekerRepository;
import com.rev.app.repository.ResumeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class JobServiceImpl implements JobService {

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private EmployerRepository employerRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private AppMapper mapper;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private JobSeekerRepository jobSeekerRepository;

    @Override
    public JobDTO createJob(JobDTO jobDTO) {
        Employer employer = employerRepository.findById(jobDTO.getEmployerId())
                .orElseThrow(() -> new ResourceNotFoundException("Employer not found"));
        Company company = companyRepository.findById(jobDTO.getCompanyId())
                .orElseThrow(() -> new ResourceNotFoundException("Company not found"));

        Job job = mapper.toJobEntity(jobDTO, employer, company);
        Job savedJob = jobRepository.save(job);

        // Notify matching seekers
        notifyMatchingSeekers(savedJob);

        return mapper.toJobDTO(savedJob);
    }

    private void notifyMatchingSeekers(Job job) {
        String jobSkills = job.getSkillsRequired() != null ? job.getSkillsRequired().toLowerCase() : "";
        List<com.rev.app.entity.JobSeeker> seekers = jobSeekerRepository.findAll();
        for (com.rev.app.entity.JobSeeker seeker : seekers) {
            String seekerSkills = seeker.getResume() != null && seeker.getResume().getSkills() != null
                    ? seeker.getResume().getSkills().toLowerCase()
                    : "";
            if (!seekerSkills.isEmpty()) {
                String[] skills = seekerSkills.split(",");
                for (String s : skills) {
                    if (jobSkills.contains(s.trim())) {
                        notificationService.sendNotification(seeker.getUser().getId(),
                                "New Job Match: " + job.getTitle() + " at " + job.getCompany().getName());
                        break;
                    }
                }
            }
        }
    }

    @Override
    public JobDTO updateJob(Long id, JobDTO jobDTO) {
        Job existingJob = jobRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found"));

        existingJob.setTitle(jobDTO.getTitle());
        existingJob.setDescription(jobDTO.getDescription());
        existingJob.setSkillsRequired(jobDTO.getSkillsRequired());
        existingJob.setExperienceRequired(jobDTO.getExperienceRequired());
        existingJob.setEducationRequired(jobDTO.getEducationRequired());
        existingJob.setLocation(jobDTO.getLocation());
        existingJob.setSalaryRange(jobDTO.getSalaryRange());
        existingJob.setJobType(jobDTO.getJobType());
        existingJob.setDeadline(jobDTO.getDeadline());
        existingJob.setNumberOfOpenings(jobDTO.getNumberOfOpenings());
        existingJob.setStatus(jobDTO.getStatus());

        return mapper.toJobDTO(jobRepository.save(existingJob));
    }

    @Override
    public void deleteJob(Long id) {
        jobRepository.deleteById(id);
    }

    @Override
    public JobDTO getJobById(Long id) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found"));
        JobDTO dto = mapper.toJobDTO(job);
        dto.setApplicantCount(applicationRepository.countByJob(job));
        return dto;
    }

    @Override
    public List<JobDTO> getAllActiveJobs() {
        return jobRepository.findByIsClosedFalse().stream()
                .map(job -> {
                    JobDTO dto = mapper.toJobDTO(job);
                    dto.setApplicantCount(applicationRepository.countByJob(job));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<JobDTO> getJobsByEmployer(Long employerId) {
        Employer employer = employerRepository.findById(employerId)
                .orElseThrow(() -> new ResourceNotFoundException("Employer not found"));
        return jobRepository.findByEmployer(employer).stream()
                .map(job -> {
                    JobDTO dto = mapper.toJobDTO(job);
                    dto.setApplicantCount(applicationRepository.countByJob(job));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<JobDTO> searchJobs(String keyword) {
        return searchJobsAdvanced(keyword, null, null, null, null, null);
    }

    @Override
    public List<JobDTO> searchJobsAdvanced(String keyword, String location, String jobType, Integer minExp,
                                           Double minSalary, java.time.LocalDate startDate) {
        String keywordParam = (keyword != null && !keyword.isEmpty()) ? "%" + keyword.toLowerCase() + "%" : null;
        String locationParam = (location != null && !location.isEmpty()) ? "%" + location.toLowerCase() + "%" : null;
        String jobTypeParam = (jobType != null && !jobType.isEmpty()) ? jobType : null;

        return jobRepository.searchJobsAdvanced(keywordParam, locationParam, jobTypeParam, minExp, minSalary, startDate)
                .stream()
                .map(mapper::toJobDTO)
                .collect(Collectors.toList());
    }

    @Autowired
    private ResumeRepository resumeRepository;

    @Override
    public List<JobDTO> getRecommendedJobs(Long seekerId) {
        return resumeRepository.findByJobSeekerId(seekerId).map(resume -> {
            String skills = resume.getSkills();
            if (skills == null || skills.isEmpty()) {
                return getAllActiveJobs();
            }
            // Simple split and search by first few skills
            String[] skillArr = skills.split(",");
            String primarySkill = skillArr[0].trim();
            return searchJobs(primarySkill);
        }).orElse(getAllActiveJobs());
    }

    @Override
    public void closeJob(Long id) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found"));
        job.setIsClosed(true);
        jobRepository.save(job);
    }

    @Override
    public void reopenJob(Long id) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found"));
        job.setIsClosed(false);
        jobRepository.save(job);
    }

    @Override
    public void markJobAsFilled(Long id) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found"));
        job.setStatus("FILLED");
        job.setIsClosed(true);
        jobRepository.save(job);
    }
}
