package com.rev.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobDTO {
    private Long id;

    @NotBlank(message = "Job title is required")
    private String title;

    private String description;

    @NotBlank(message = "Required skills are mandatory")
    private String skillsRequired;

    @NotNull(message = "Experience requirement is required")
    @Min(value = 0, message = "Experience cannot be negative")
    private Integer experienceRequired;

    private String educationRequired;

    @NotBlank(message = "Location is required")
    private String location;

    @NotNull(message = "Salary range is required")
    @Positive(message = "Salary must be positive")
    private Double salaryRange;

    @NotBlank(message = "Job type is required")
    private String jobType;

    @NotNull(message = "Deadline is required")
    @Future(message = "Deadline must be in the future")
    @DateTimeFormat(iso = ISO.DATE)
    private LocalDate deadline;

    @NotNull(message = "Number of openings is required")
    @Min(value = 1, message = "Openings must be at least 1")
    private Integer numberOfOpenings;

    private Boolean isClosed;
    private String status;
    private Long employerId;
    private Long companyId;
    private String companyName;
    private String companyIndustry;
    private String companyWebsite;
    private String companySize;
    private String companyDescription;

    @DateTimeFormat(iso = ISO.DATE)
    private LocalDate postedDate;

    private Long applicantCount;
    private Boolean isApplied;
}
