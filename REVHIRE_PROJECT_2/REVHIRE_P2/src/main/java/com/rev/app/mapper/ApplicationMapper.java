package com.rev.app.mapper;

import com.rev.app.dto.ApplicationDTO;
import com.rev.app.dto.NoteDTO;
import com.rev.app.dto.SavedJobDTO;
import com.rev.app.entity.Application;
import com.rev.app.entity.ApplicationNote;
import com.rev.app.entity.SavedJob;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class ApplicationMapper {

    public ApplicationDTO toApplicationDTO(Application app) {
        if (app == null)
            return null;
        ApplicationDTO dto = new ApplicationDTO();
        dto.setId(app.getId());
        dto.setJobId(app.getJob().getId());
        dto.setJobTitle(app.getJob().getTitle());
        dto.setCompanyName(app.getJob().getCompany().getName());
        dto.setJobSeekerId(app.getJobSeeker().getId());
        dto.setSeekerName(app.getJobSeeker().getName());
        dto.setResumeId(app.getResume().getId());
        dto.setCoverLetter(app.getCoverLetter());
        dto.setStatus(app.getStatus());
        dto.setAppliedDate(app.getAppliedDate());
        dto.setWithdrawReason(app.getWithdrawReason());
        if (app.getApplicationNotes() != null) {
            dto.setNotes(app.getApplicationNotes().stream()
                    .map(this::toNoteDTO)
                    .collect(Collectors.toList()));
        }
        return dto;
    }

    public NoteDTO toNoteDTO(ApplicationNote note) {
        if (note == null)
            return null;
        NoteDTO dto = new NoteDTO();
        dto.setId(note.getId());
        dto.setNote(note.getNote());
        dto.setCreatedAt(note.getCreatedAt());
        return dto;
    }

    public SavedJobDTO toSavedJobDTO(SavedJob savedJob) {
        if (savedJob == null)
            return null;
        SavedJobDTO dto = new SavedJobDTO();
        dto.setId(savedJob.getId());
        dto.setJobSeekerId(savedJob.getJobSeeker().getId());
        dto.setJobId(savedJob.getJob().getId());
        dto.setJobTitle(savedJob.getJob().getTitle());
        dto.setCompanyName(savedJob.getJob().getCompany().getName());
        dto.setLocation(savedJob.getJob().getLocation());
        dto.setSalaryRange(savedJob.getJob().getSalaryRange());
        dto.setSavedAt(savedJob.getSavedAt());
        return dto;
    }
}
