package com.rev.app.controller;

import com.rev.app.dto.UserDTO;
import com.rev.app.dto.JobDTO;
import com.rev.app.dto.ApplicationDTO;
import com.rev.app.entity.Application;
import com.rev.app.entity.User;
import java.util.List;
import com.rev.app.exception.ResourceNotFoundException;
import com.rev.app.repository.JobSeekerRepository;
import com.rev.app.repository.EmployerRepository;
import com.rev.app.service.UserService;
import com.rev.app.service.ApplicationService;
import com.rev.app.service.JobService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;

@Controller
public class UserController {

    private static final Logger logger = LogManager.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private JobSeekerRepository jobSeekerRepository;

    @Autowired
    private EmployerRepository employerRepository;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private JobService jobService;

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("userDTO", new UserDTO());
        return "auth/register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("userDTO") UserDTO userDTO,
                               BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "auth/register";
        }
        if (userService.existsByEmail(userDTO.getEmail())) {
            model.addAttribute("error", "Email already registered. Please login.");
            return "auth/register";
        }
        logger.info("Registering new user: {}", userDTO.getEmail());
        userService.registerUser(userDTO);
        return "redirect:/login?registered";
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "auth/login";
    }

    @GetMapping("/dashboard")
    public String dashboard(Principal principal, Model model) {
        User user = userService.findByEmail(principal.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        model.addAttribute("user", user);

        if (user.getRole() == User.Role.JOB_SEEKER) {
            com.rev.app.entity.JobSeeker seeker = jobSeekerRepository.findByUserId(user.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Job Seeker profile not found"));
            model.addAttribute("seeker", seeker);
            model.addAttribute("recentApplications", applicationService.getApplicationsBySeeker(seeker.getId()));
            model.addAttribute("recommendedJobs", jobService.getRecommendedJobs(seeker.getId()));
            return "seeker/dashboard";
        } else {
            com.rev.app.entity.Employer employer = employerRepository.findByUserId(user.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Employer profile not found"));

            List<JobDTO> jobs = jobService.getJobsByEmployer(employer.getId());
            long totalJobs = jobs.size();
            long activeJobs = jobs.stream().filter(j -> !j.getIsClosed()).count();

            long totalApplications = 0;
            long pendingReviews = 0;
            for (JobDTO job : jobs) {
                List<ApplicationDTO> apps = applicationService.getApplicationsByJob(job.getId());
                totalApplications += apps.size();
                pendingReviews += apps.stream().filter(a -> a.getStatus() == Application.ApplicationStatus.APPLIED)
                        .count();
            }

            model.addAttribute("employer", employer);
            model.addAttribute("totalJobs", totalJobs);
            model.addAttribute("activeJobs", activeJobs);
            model.addAttribute("totalApplications", totalApplications);
            model.addAttribute("pendingReviews", pendingReviews);

            return "employer/dashboard";
        }
    }
}
