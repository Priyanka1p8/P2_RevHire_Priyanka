package com.rev.app.repository;

import com.rev.app.entity.SavedJob;
import com.rev.app.entity.JobSeeker;
import com.rev.app.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface SavedJobRepository extends JpaRepository<SavedJob, Long> {
    List<SavedJob> findByJobSeeker(JobSeeker jobSeeker);

    Optional<SavedJob> findByJobSeekerAndJob(JobSeeker jobSeeker, Job job);

    boolean existsByJobSeekerAndJob(JobSeeker jobSeeker, Job job);
}
