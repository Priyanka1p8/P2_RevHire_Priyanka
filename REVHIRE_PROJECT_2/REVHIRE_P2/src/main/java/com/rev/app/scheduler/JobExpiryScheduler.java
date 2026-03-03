package com.rev.app.scheduler;

import com.rev.app.entity.Job;
import com.rev.app.repository.JobRepository;
import com.rev.app.service.NotificationService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class JobExpiryScheduler {

    private static final Logger logger = LogManager.getLogger(JobExpiryScheduler.class);

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private NotificationService notificationService;

    // Run every day at midnight
    @Scheduled(cron = "0 0 0 * * *")
    public void notifyExpiringJobs() {
        LocalDate reminderDate = LocalDate.now().plusDays(2);
        logger.info("Checking for jobs expiring on {}", reminderDate);

        List<Job> expiringJobs = jobRepository.findByDeadlineAndIsClosedFalse(reminderDate);

        for (Job job : expiringJobs) {
            String message = "Reminder: Your job posting '" + job.getTitle() + "' will expire in 2 days (on "
                    + job.getDeadline() + ").";
            notificationService.sendNotification(job.getEmployer().getUser().getId(), message);
            logger.info("Sent expiry reminder to employer {} for job {}", job.getEmployer().getId(), job.getId());
        }
    }
}
