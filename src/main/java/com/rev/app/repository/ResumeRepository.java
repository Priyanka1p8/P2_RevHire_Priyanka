package com.rev.app.repository;

import com.rev.app.entity.Resume;
import com.rev.app.entity.JobSeeker;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ResumeRepository extends JpaRepository<Resume, Long> {
    Optional<Resume> findByJobSeeker(JobSeeker jobSeeker);

    Optional<Resume> findByJobSeekerId(Long jobSeekerId);
}
