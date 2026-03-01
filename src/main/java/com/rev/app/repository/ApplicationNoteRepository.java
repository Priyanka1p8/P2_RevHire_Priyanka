package com.rev.app.repository;

import com.rev.app.entity.ApplicationNote;
import com.rev.app.entity.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ApplicationNoteRepository extends JpaRepository<ApplicationNote, Long> {
    List<ApplicationNote> findByApplication(Application application);
}
