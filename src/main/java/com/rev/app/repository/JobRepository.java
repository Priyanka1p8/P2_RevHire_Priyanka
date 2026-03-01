package com.rev.app.repository;

import com.rev.app.entity.Job;
import com.rev.app.entity.Employer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface JobRepository extends JpaRepository<Job, Long> {
    List<Job> findByEmployer(Employer employer);

    List<Job> findByIsClosedFalse();

    List<Job> findByDeadlineAndIsClosedFalse(java.time.LocalDate deadline);

    @Query("SELECT j FROM Job j WHERE j.isClosed = false AND " +
            "(:keyword IS NULL OR LOWER(j.title) LIKE :keyword OR " +
            "LOWER(j.skillsRequired) LIKE :keyword OR " +
            "LOWER(j.company.name) LIKE :keyword) AND " +
            "(:location IS NULL OR LOWER(j.location) LIKE :location) AND " +
            "(:jobType IS NULL OR j.jobType = :jobType) AND " +
            "(:minExp IS NULL OR j.experienceRequired >= :minExp) AND " +
            "(:minSalary IS NULL OR j.salaryRange >= :minSalary) AND " +
            "(:startDate IS NULL OR j.postedDate >= :startDate)")
    List<Job> searchJobsAdvanced(
            @Param("keyword") String keyword,
            @Param("location") String location,
            @Param("jobType") String jobType,
            @Param("minExp") Integer minExp,
            @Param("minSalary") Double minSalary,
            @Param("startDate") java.time.LocalDate startDate);
}
