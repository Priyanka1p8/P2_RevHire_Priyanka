package com.rev.app.dto;

import com.rev.app.entity.Application.ApplicationStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationDTO {
    private Long id;
    private Long jobId;
    private String jobTitle;
    private String companyName;
    private Long jobSeekerId;
    private String seekerName;
    private Long resumeId;
    private String coverLetter;
    private ApplicationStatus status;
    private LocalDateTime appliedDate;
    private String withdrawReason;
    private List<NoteDTO> notes;
}
