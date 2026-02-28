package com.rev.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobSeekerDTO {
    private Long id;
    private Long userId;
    private String name;
    private String phone;
    private String location;
    private String employmentStatus;
    private String email; // From User entity
}
