package com.rev.app.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "resumes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Resume {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "job_seeker_id")
    private JobSeeker jobSeeker;

    @Column(length = 2000)
    private String objective; // Textual section: objective/summary

    @Column(length = 2000)
    private String education;

    @Column(length = 2000)
    private String experience;

    @Column(length = 1000)
    private String skills;

    @Column(length = 1000)
    private String projects;

    @Column(length = 1000)
    private String certifications;

    private String filePath; // Path to uploaded PDF/DOCX
    private String fileName;
}
