package com.rev.app.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SavedJobDTO {
    private Long id;
    private Long jobSeekerId;
    private Long jobId;
    private String jobTitle;
    private String companyName;
    private String location;
    private Double salaryRange;
    private LocalDateTime savedAt;
}
