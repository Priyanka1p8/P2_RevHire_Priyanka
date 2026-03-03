package com.rev.app.service;

import com.rev.app.dto.ResumeDTO;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

public interface ResumeService {
    ResumeDTO createOrUpdateResume(ResumeDTO resumeDTO);

    ResumeDTO getResumeBySeekerId(Long seekerId);

    void uploadResumeFile(Long seekerId, MultipartFile file) throws IOException;

    void deleteResumeFile(Long seekerId);
}
