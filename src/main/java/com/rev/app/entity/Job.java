package com.rev.app.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "jobs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(length = 2000)
    private String description;

    @Column(nullable = false)
    private String skillsRequired;

    private Integer experienceRequired;
    private String educationRequired;

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    private Double salaryRange;

    @Column(nullable = false)
    private String jobType; // Full-time, Internship, etc.

    @Column(nullable = false)
    private LocalDate deadline;

    private Integer numberOfOpenings;
    private Boolean isClosed = false;
    private String status; // Open, Closed, etc.

    @ManyToOne
    @JoinColumn(name = "employer_id")
    private Employer employer;

    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;

    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL, orphanRemoval = true)
    private java.util.List<Application> applications;

    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL, orphanRemoval = true)
    private java.util.List<SavedJob> savedJobs;

    private LocalDate postedDate = LocalDate.now();
}

