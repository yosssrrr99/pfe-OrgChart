package com.OrgChart.springsecurity.dto;

import com.OrgChart.springsecurity.entities.Role;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.*;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {


    private String firstName;
    private String lastName;
    private String mailAddress;
    private Double phoneNumber;
    private String grade;
    private String poste;
    private byte[] image;
    private String qualifications;
    private String nomorg;
    private Date DateEmployment;
    private String password;
    private Role role;
}
