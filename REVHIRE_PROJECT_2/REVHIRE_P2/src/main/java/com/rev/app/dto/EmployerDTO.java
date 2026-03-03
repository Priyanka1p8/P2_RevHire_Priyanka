package com.rev.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployerDTO {
    private Long id;
    private Long userId;
    private Long companyId;
    private String contactPerson;
    private String designation;
    private String email;
    private CompanyDTO company;
}
