package com.rev.app.repository;

import com.rev.app.entity.Employer;
import com.rev.app.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface EmployerRepository extends JpaRepository<Employer, Long> {
    Optional<Employer> findByUser(User user);

    Optional<Employer> findByUserId(Long userId);
}
