package com.rev.app.controller;

import com.rev.app.dto.ApplicationDTO;
import com.rev.app.dto.JobDTO;
import com.rev.app.entity.Company;
import com.rev.app.entity.Employer;
import com.rev.app.entity.User;
import com.rev.app.exception.ResourceNotFoundException;
import com.rev.app.repository.CompanyRepository;
import com.rev.app.repository.EmployerRepository;
import com.rev.app.service.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/employer")
public class EmployerController {

    private static final Logger logger = LogManager.getLogger(EmployerController.class);

    @Autowired
    private UserService userService;
    @Autowired
    private JobService jobService;
    @Autowired
    private ApplicationService applicationService;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private EmployerRepository employerRepository;
    @Autowired
    private CompanyRepository companyRepository;
    @Autowired
    private ResumeService resumeService;

    private Employer getEmployer(Principal principal) {
        User user = userService.findByEmail(principal.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return employerRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Employer profile not found"));
    }

    // --- Company Profile ---
    @GetMapping("/profile")
    public String viewProfile(Principal principal, Model model) {
        Employer employer = getEmployer(principal);
        model.addAttribute("employer", employer);
        model.addAttribute("company", employer.getCompany());
        return "employer/profile";
    }

    @PostMapping("/profile")
    public String updateProfile(@ModelAttribute Company companyData,
                                @RequestParam String contactPerson,
                                @RequestParam String designation,
                                Principal principal) {
        Employer employer = getEmployer(principal);

        // Update Employer info
        employer.setContactPerson(contactPerson);
        employer.setDesignation(designation);
        employerRepository.save(employer);

        // Update Company info
        Company company = employer.getCompany();
        company.setName(companyData.getName());
        company.setIndustry(companyData.getIndustry());
        company.setSize(companyData.getSize());
        company.setDescription(companyData.getDescription());
        company.setWebsite(companyData.getWebsite());
        company.setLocation(companyData.getLocation());
        companyRepository.save(company);

        return "redirect:/employer/profile?updated";
    }

    // --- Job Management ---
    @GetMapping("/jobs")
    public String listMyJobs(@RequestParam(required = false) String keyword,
                             @RequestParam(required = false) String status,
                             Principal principal, Model model) {
        Employer employer = getEmployer(principal);
        List<JobDTO> jobs = jobService.getJobsByEmployer(employer.getId());

        // Perform filtering
        if (keyword != null && !keyword.isEmpty()) {
            String k = keyword.toLowerCase();
            jobs = jobs.stream()
                    .filter(j -> j.getTitle().toLowerCase().contains(k) ||
                            j.getLocation().toLowerCase().contains(k))
                    .collect(Collectors.toList());
        }

        if (status != null && !status.isEmpty()) {
            if ("CLOSED".equals(status)) {
                jobs = jobs.stream().filter(j -> j.getIsClosed() && !"FILLED".equals(j.getStatus()))
                        .collect(Collectors.toList());
            } else if ("ACTIVE".equals(status)) {
                jobs = jobs.stream().filter(j -> !j.getIsClosed()).collect(Collectors.toList());
            } else if ("FILLED".equals(status)) {
                jobs = jobs.stream().filter(j -> "FILLED".equals(j.getStatus())).collect(Collectors.toList());
            }
        }

        // Dashboard stats (from original unfiltered list)
        List<JobDTO> allJobs = jobService.getJobsByEmployer(employer.getId());
        long totalJobs = allJobs.size();
        long activeJobs = allJobs.stream().filter(j -> !j.getIsClosed()).count();
        long closedJobs = allJobs.stream().filter(j -> j.getIsClosed() && !"FILLED".equals(j.getStatus())).count();
        long filledJobs = allJobs.stream().filter(j -> "FILLED".equals(j.getStatus())).count();

        model.addAttribute("jobs", jobs);
        model.addAttribute("totalJobs", totalJobs);
        model.addAttribute("activeJobs", activeJobs);
        model.addAttribute("closedJobs", closedJobs);
        model.addAttribute("filledJobs", filledJobs);
        model.addAttribute("keyword", keyword);
        model.addAttribute("status", status);

        return "employer/jobs";
    }

    @GetMapping("/jobs/new")
    public String newJobForm(Principal principal, Model model) {
        Employer employer = getEmployer(principal);
        model.addAttribute("jobDTO", new JobDTO());
        model.addAttribute("employerId", employer.getId());
        model.addAttribute("companyId", employer.getCompany().getId());
        return "employer/job-form";
    }

    @PostMapping("/jobs/new")
    public String createJob(@Valid @ModelAttribute("jobDTO") JobDTO jobDTO, BindingResult result,
                            Principal principal, Model model) {
        if (result.hasErrors()) {
            Employer employer = getEmployer(principal);
            model.addAttribute("employerId", employer.getId());
            model.addAttribute("companyId", employer.getCompany().getId());
            return "employer/job-form";
        }
        Employer employer = getEmployer(principal);
        jobDTO.setEmployerId(employer.getId());
        jobDTO.setCompanyId(employer.getCompany().getId());
        jobService.createJob(jobDTO);
        logger.info("Employer {} created a new job: {}", employer.getId(), jobDTO.getTitle());
        return "redirect:/employer/jobs?created";
    }

    @GetMapping("/jobs/{id}/edit")
    public String editJobForm(@PathVariable Long id, Model model) {
        model.addAttribute("jobDTO", jobService.getJobById(id));
        return "employer/job-form";
    }

    @PostMapping("/jobs/{id}/edit")
    public String updateJob(@PathVariable Long id, @Valid @ModelAttribute("jobDTO") JobDTO jobDTO,
                            BindingResult result) {
        if (result.hasErrors()) {
            return "employer/job-form";
        }
        jobService.updateJob(id, jobDTO);
        return "redirect:/employer/jobs?updated";
    }

    @PostMapping("/jobs/{id}/close")
    public String closeJob(@PathVariable Long id) {
        jobService.closeJob(id);
        return "redirect:/employer/jobs?closed";
    }

    @PostMapping("/jobs/{id}/reopen")
    public String reopenJob(@PathVariable Long id) {
        jobService.reopenJob(id);
        return "redirect:/employer/jobs?reopened";
    }

    @PostMapping("/jobs/{id}/mark-filled")
    public String markAsFilled(@PathVariable Long id) {
        jobService.markJobAsFilled(id);
        return "redirect:/employer/jobs?filled";
    }

    @PostMapping("/jobs/{id}/delete")
    public String deleteJob(@PathVariable Long id) {
        jobService.deleteJob(id);
        return "redirect:/employer/jobs?deleted";
    }

    // --- Applicant Management ---
    @GetMapping("/jobs/{jobId}/applicants")
    public String viewApplicants(@PathVariable Long jobId,
                                 @RequestParam(required = false) String status,
                                 @RequestParam(required = false) String keyword,
                                 @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate startDate,
                                 Model model) {
        model.addAttribute("job", jobService.getJobById(jobId));
        model.addAttribute("applications", applicationService.searchApplications(jobId, status, keyword, startDate));
        model.addAttribute("status", status);
        model.addAttribute("keyword", keyword);
        model.addAttribute("startDate", startDate);
        return "employer/applicants";
    }

    @PostMapping("/applications/{id}/status")
    public String updateStatus(@PathVariable Long id,
                               @RequestParam String status,
                               @RequestParam(required = false) String comment,
                               @RequestParam Long jobId,
                               Principal principal) {
        Employer employer = getEmployer(principal);
        ApplicationDTO updated = applicationService.updateApplicationStatus(id, status, comment);
        logger.info("Employer {} updated application {} to status {}", employer.getId(), id, status);
        // Notify the seeker
        notificationService.sendNotificationToSeeker(
                updated.getJobSeekerId(),
                "Your application for '" + updated.getJobTitle() + "' has been updated to: " + status);
        return "redirect:/employer/jobs/" + jobId + "/applicants?updated";
    }

    @PostMapping("/applications/bulk-status")
    public String updateStatusBulk(@RequestParam(value = "ids", required = false) List<Long> ids,
                                   @RequestParam String status,
                                   @RequestParam(required = false) String comment,
                                   @RequestParam Long jobId,
                                   org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {

        if (ids == null || ids.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Please select at least one application.");
            return "redirect:/employer/jobs/" + jobId + "/applicants";
        }

        applicationService.updateStatusBulk(ids, status, comment);
        // Notify each affected seeker
        List<ApplicationDTO> apps = applicationService.getApplicationsByJob(jobId);
        apps.stream().filter(a -> ids.contains(a.getId())).forEach(a -> {
            try {
                notificationService.sendNotificationToSeeker(
                        a.getJobSeekerId(),
                        "Your application for '" + a.getJobTitle() + "' has been updated to: " + status);
            } catch (Exception e) {
                logger.warn("Failed to send notification to seeker {}: {}", a.getJobSeekerId(), e.getMessage());
            }
        });
        return "redirect:/employer/jobs/" + jobId + "/applicants?bulk_updated";
    }

    @PostMapping("/applications/{id}/notes")
    public String addNote(@PathVariable Long id, @RequestParam String note, @RequestParam Long jobId) {
        applicationService.addNoteToApplication(id, note);
        return "redirect:/employer/jobs/" + jobId + "/applicants?note_added";
    }

    @GetMapping("/resumes/{seekerId}")
    public String viewResume(@PathVariable Long seekerId, Model model) {
        model.addAttribute("resume", resumeService.getResumeBySeekerId(seekerId));
        return "employer/resume-view";
    }

    @GetMapping("/notifications")
    public String viewNotifications(Principal principal, Model model) {
        User user = userService.findByEmail(principal.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        model.addAttribute("notifications", notificationService.getNotificationsForUser(user.getId()));
        return "employer/notifications";
    }

    @PostMapping("/notifications/{id}/read")
    public String markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return "redirect:/employer/notifications";
    }

    @PostMapping("/applications/notes/{id}/update")
    public String updateNote(@PathVariable Long id, @RequestParam String note, @RequestParam Long jobId) {
        applicationService.updateNote(id, note);
        return "redirect:/employer/jobs/" + jobId + "/applicants?note_updated";
    }

    @PostMapping("/applications/notes/{id}/delete")
    public String deleteNote(@PathVariable Long id, @RequestParam Long jobId) {
        applicationService.deleteNote(id);
        return "redirect:/employer/jobs/" + jobId + "/applicants?note_deleted";
    }
}
