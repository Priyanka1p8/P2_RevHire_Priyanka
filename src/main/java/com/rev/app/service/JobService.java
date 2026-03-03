package com.rev.app.service;

import com.rev.app.dto.JobDTO;
import java.util.List;

public interface JobService {
    JobDTO createJob(JobDTO jobDTO);

    JobDTO updateJob(Long id, JobDTO jobDTO);

    void deleteJob(Long id);

    JobDTO getJobById(Long id);

    List<JobDTO> getAllActiveJobs();

    List<JobDTO> getJobsByEmployer(Long employerId);

    List<JobDTO> searchJobs(String keyword);

    List<JobDTO> searchJobsAdvanced(String keyword, String location, String jobType, Integer minExp, Double minSalary,
                                    java.time.LocalDate startDate);

    List<JobDTO> getRecommendedJobs(Long seekerId);

    void closeJob(Long id);

    void reopenJob(Long id);

    void markJobAsFilled(Long id);
}
