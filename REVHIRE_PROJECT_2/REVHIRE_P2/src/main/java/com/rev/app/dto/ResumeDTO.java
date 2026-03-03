package com.rev.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResumeDTO {
    private Long id;
    private Long jobSeekerId;
    private String objective;
    private String education;
    private String experience;
    private String skills;
    private String projects;
    private String certifications;
    private String filePath;
    private String fileName;
    private String seekerName;
    private String seekerEmail;
    private String seekerPhone;
    private String seekerLocation;
    private String seekerEmploymentStatus;
}
