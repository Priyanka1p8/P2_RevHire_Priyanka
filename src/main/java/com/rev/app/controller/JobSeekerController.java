package com.rev.app.controller;

import com.rev.app.dto.JobDTO;
import com.rev.app.dto.ResumeDTO;
import com.rev.app.dto.ApplicationDTO;
import com.rev.app.entity.JobSeeker;
import com.rev.app.entity.User;
import com.rev.app.exception.DuplicateApplicationException;
import com.rev.app.exception.ResourceNotFoundException;
import com.rev.app.repository.JobSeekerRepository;
import com.rev.app.service.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/seeker")
public class JobSeekerController {

    private static final Logger logger = LogManager.getLogger(JobSeekerController.class);

    @Autowired
    private UserService userService;
    @Autowired
    private JobService jobService;
    @Autowired
    private ResumeService resumeService;
    @Autowired
    private ApplicationService applicationService;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private SavedJobService savedJobService;
    @Autowired
    private JobSeekerRepository jobSeekerRepository;

    private JobSeeker getSeeker(Principal principal) {
        User user = userService.findByEmail(principal.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return jobSeekerRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));
    }

    // --- Profile ---
    @GetMapping("/profile")
    public String viewProfile(Principal principal, Model model) {
        JobSeeker seeker = getSeeker(principal);
        model.addAttribute("seeker", seeker);
        return "seeker/profile";
    }

    @PostMapping("/profile")
    public String updateProfile(@ModelAttribute JobSeeker updated, Principal principal) {
        JobSeeker seeker = getSeeker(principal);
        seeker.setName(updated.getName());
        seeker.setPhone(updated.getPhone());
        seeker.setLocation(updated.getLocation());
        seeker.setEmploymentStatus(updated.getEmploymentStatus());
        jobSeekerRepository.save(seeker);
        return "redirect:/seeker/profile?updated";
    }

    // --- Resume ---
    @GetMapping("/resume")
    public String viewResume(Principal principal, Model model) {
        JobSeeker seeker = getSeeker(principal);
        populateResumeModel(seeker, model);
        return "seeker/resume";
    }

    @PostMapping("/resume")
    public String saveResume(@ModelAttribute ResumeDTO resumeDTO, Principal principal) {
        JobSeeker seeker = getSeeker(principal);
        resumeDTO.setJobSeekerId(seeker.getId());
        resumeService.createOrUpdateResume(resumeDTO);
        return "redirect:/seeker/resume?saved";
    }

    @PostMapping("/resume/upload")
    public String uploadResume(@RequestParam("file") MultipartFile file,
                               Principal principal, Model model) {
        JobSeeker seeker = getSeeker(principal);
        if (file.isEmpty()) {
            model.addAttribute("uploadError", "Please select a file.");
            populateResumeModel(seeker, model);
            return "seeker/resume";
        }
        String contentType = file.getContentType();
        if (contentType == null ||
                (!contentType.equals("application/pdf") &&
                        !contentType
                                .equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document"))) {
            model.addAttribute("uploadError", "Only PDF or DOCX files are allowed.");
            populateResumeModel(seeker, model);
            return "seeker/resume";
        }
        try {
            resumeService.uploadResumeFile(seeker.getId(), file);
        } catch (IOException e) {
            logger.error("File upload error for seeker {}: {}", seeker.getId(), e.getMessage());
            model.addAttribute("uploadError", "Upload failed. Please try again.");
            populateResumeModel(seeker, model);
            return "seeker/resume";
        }
        return "redirect:/seeker/resume?uploaded";
    }

    private void populateResumeModel(JobSeeker seeker, Model model) {
        try {
            ResumeDTO resume = resumeService.getResumeBySeekerId(seeker.getId());
            model.addAttribute("resume", resume);
        } catch (ResourceNotFoundException e) {
            model.addAttribute("resume", new ResumeDTO());
        }
        model.addAttribute("seekerId", seeker.getId());
    }

    // --- Resume Download ---
    @GetMapping("/resume/download")
    @ResponseBody
    public ResponseEntity<Resource> downloadResume(@RequestParam Long id) {
        ResumeDTO resume = resumeService.getResumeBySeekerId(id);
        if (resume.getFilePath() == null || resume.getFilePath().isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        try {
            Path filePath = Paths.get(resume.getFilePath());
            Resource resource = new UrlResource(java.util.Objects.requireNonNull(filePath.toUri()));
            if (resource.exists() && resource.isReadable()) {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION,
                                "attachment; filename=\"" + resume.getFileName() + "\"")
                        .body(resource);
            }
        } catch (MalformedURLException e) {
            logger.error("Resume download failed for seeker {}: {}", id, e.getMessage());
        }
        return ResponseEntity.notFound().build();
    }

    // --- Job Search ---
    @GetMapping("/jobs")
    public String searchJobs(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String jobType,
            @RequestParam(required = false) Integer minExp,
            @RequestParam(required = false) Double minSalary,
            @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate startDate,
            Principal principal,
            Model model) {

        List<JobDTO> jobs = jobService.searchJobsAdvanced(keyword, location, jobType, minExp, minSalary, startDate);

        if (principal != null) {
            JobSeeker seeker = getSeeker(principal);
            List<Long> appliedJobIds = applicationService.getAppliedJobIds(seeker.getId());
            jobs.forEach(job -> job.setIsApplied(appliedJobIds.contains(job.getId())));
        }

        model.addAttribute("jobs", jobs);
        model.addAttribute("keyword", keyword);
        model.addAttribute("location", location);
        model.addAttribute("jobType", jobType);
        model.addAttribute("minExp", minExp);
        model.addAttribute("minSalary", minSalary);
        model.addAttribute("startDate", startDate);

        return "seeker/jobs";
    }

    @GetMapping("/jobs/{id}")
    public String viewJob(@PathVariable Long id, Principal principal, Model model) {
        model.addAttribute("job", jobService.getJobById(id));
        JobSeeker seeker = getSeeker(principal);
        try {
            ResumeDTO resume = resumeService.getResumeBySeekerId(seeker.getId());
            model.addAttribute("resume", resume);
        } catch (ResourceNotFoundException ignored) {
        }
        model.addAttribute("seekerId", seeker.getId());
        model.addAttribute("isSaved", savedJobService.isJobSaved(seeker.getId(), id));
        model.addAttribute("isApplied", applicationService.hasApplied(seeker.getId(), id));
        return "seeker/job-detail";
    }

    // --- Applications ---
    @PostMapping("/apply")
    public String applyToJob(@ModelAttribute ApplicationDTO appDTO, Principal principal) {
        JobSeeker seeker = getSeeker(principal);
        appDTO.setJobSeekerId(seeker.getId());
        try {
            applicationService.applyToJob(appDTO);
            logger.info("Seeker {} applied to job {}", seeker.getId(), appDTO.getJobId());
            notificationService.sendNotification(seeker.getUser().getId(),
                    "You have successfully applied for Job ID: " + appDTO.getJobId());
            return "redirect:/seeker/applications?applied";
        } catch (DuplicateApplicationException e) {
            logger.warn("Seeker {} already applied to job {}", seeker.getId(), appDTO.getJobId());
            return "redirect:/seeker/jobs/" + appDTO.getJobId() + "?alreadyApplied";
        }
    }

    @GetMapping("/applications")
    public String viewApplications(Principal principal, Model model) {
        JobSeeker seeker = getSeeker(principal);
        model.addAttribute("applications", applicationService.getApplicationsBySeeker(seeker.getId()));
        return "seeker/applications";
    }

    @PostMapping("/applications/{id}/withdraw")
    public String withdrawApplication(@PathVariable Long id,
                                      @RequestParam(required = false) String reason,
                                      Principal principal) {
        applicationService.withdrawApplication(id, reason);
        logger.info("Application {} withdrawn", id);
        return "redirect:/seeker/applications?withdrawn";
    }

    // --- Saved Jobs ---
    @GetMapping("/saved-jobs")
    public String viewSavedJobs(Principal principal, Model model) {
        JobSeeker seeker = getSeeker(principal);
        model.addAttribute("savedJobs", savedJobService.getSavedJobsBySeeker(seeker.getId()));
        return "seeker/saved-jobs";
    }

    @PostMapping("/jobs/{id}/save")
    public String saveJob(@PathVariable Long id, Principal principal) {
        JobSeeker seeker = getSeeker(principal);
        savedJobService.saveJob(seeker.getId(), id);
        return "redirect:/seeker/jobs/" + id + "?saved";
    }

    @PostMapping("/jobs/{id}/unsave")
    public String unsaveJob(@PathVariable Long id, Principal principal,
                            @RequestParam(required = false) String redirect) {
        JobSeeker seeker = getSeeker(principal);
        savedJobService.unsaveJob(seeker.getId(), id);
        if ("saved".equals(redirect)) {
            return "redirect:/seeker/saved-jobs?unsaved";
        }
        return "redirect:/seeker/jobs/" + id + "?unsaved";
    }

    // --- Notifications ---
    @GetMapping("/notifications")
    public String viewNotifications(Principal principal, Model model) {
        User user = userService.findByEmail(principal.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        model.addAttribute("notifications", notificationService.getNotificationsForUser(user.getId()));
        notificationService.markAllRead(user.getId());
        return "seeker/notifications";
    }
}
