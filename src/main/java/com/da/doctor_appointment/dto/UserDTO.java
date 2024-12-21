package com.da.doctor_appointment.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private String id;
    private String username;
    private String name;

    @JsonProperty("create_at")
    private LocalDateTime createAt;
    
    private String email;
    private Boolean gender;
    
    @JsonProperty("date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "photo_url")
    private String photoUrl;

    private String address;
}
