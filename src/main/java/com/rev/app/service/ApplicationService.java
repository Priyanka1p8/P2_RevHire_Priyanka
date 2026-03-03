package com.rev.app.service;

import com.rev.app.dto.ApplicationDTO;
import java.util.List;

public interface ApplicationService {
    ApplicationDTO applyToJob(ApplicationDTO applicationDTO);

    List<ApplicationDTO> getApplicationsBySeeker(Long seekerId);

    List<ApplicationDTO> getApplicationsByJob(Long jobId);

    ApplicationDTO updateApplicationStatus(Long id, String status, String comment);

    void withdrawApplication(Long id, String reason);

    List<ApplicationDTO> searchApplications(Long jobId, String status, String keyword, java.time.LocalDate startDate,
                                            Integer minExp);

    void updateStatusBulk(List<Long> ids, String status, String comment);

    void addNoteToApplication(Long applicationId, String note);

    void updateNote(Long noteId, String newNote);

    void deleteNote(Long noteId);

    java.util.List<Long> getAppliedJobIds(Long seekerId);

    boolean hasApplied(Long seekerId, Long jobId);
}
