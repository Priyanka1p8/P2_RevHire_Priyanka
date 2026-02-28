package com.rev.app.repository;

import com.rev.app.entity.JobSeeker;
import com.rev.app.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface JobSeekerRepository extends JpaRepository<JobSeeker, Long> {
    Optional<JobSeeker> findByUser(User user);

    Optional<JobSeeker> findByUserId(Long userId);
}
