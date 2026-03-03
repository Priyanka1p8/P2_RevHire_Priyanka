package com.rev.app.repository;

import com.rev.app.entity.Application;
import com.rev.app.entity.Job;
import com.rev.app.entity.JobSeeker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface ApplicationRepository extends JpaRepository<Application, Long> {
    List<Application> findByJobSeeker(JobSeeker jobSeeker);

    List<Application> findByJob(Job job);

    long countByJob(Job job);

    boolean existsByJobSeekerAndJob(JobSeeker jobSeeker, Job job);

    @Query("SELECT a FROM Application a WHERE a.job.id = :jobId AND " +
            "(:status IS NULL OR a.status = :status) AND " +
            "(:startDate IS NULL OR a.appliedDate >= :startDate) AND " +
            "(:minExp IS NULL OR a.jobSeeker.experienceYears >= :minExp) AND " +
            "(:keyword IS NULL OR LOWER(a.jobSeeker.name) LIKE :keyword OR " +
            "LOWER(a.resume.skills) LIKE :keyword OR " +
            "LOWER(a.resume.education) LIKE :keyword OR " +
            "LOWER(a.resume.experience) LIKE :keyword)")
    List<Application> searchApplications(
            @Param("jobId") Long jobId,
            @Param("status") Application.ApplicationStatus status,
            @Param("keyword") String keyword,
            @Param("startDate") java.time.LocalDate startDate,
            @Param("minExp") Integer minExp);
}
