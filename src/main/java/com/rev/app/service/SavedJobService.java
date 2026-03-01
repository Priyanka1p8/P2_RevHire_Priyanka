package com.rev.app.service;

import com.rev.app.dto.SavedJobDTO;
import java.util.List;

public interface SavedJobService {
    void saveJob(Long seekerId, Long jobId);

    void unsaveJob(Long seekerId, Long jobId);

    List<SavedJobDTO> getSavedJobsBySeeker(Long seekerId);

    boolean isJobSaved(Long seekerId, Long jobId);
}
