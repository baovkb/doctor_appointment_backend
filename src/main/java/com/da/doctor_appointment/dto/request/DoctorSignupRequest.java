package com.da.doctor_appointment.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class DoctorSignupRequest extends SignupRequest{
    @NotBlank(message = "Description is mandatory")
    private String description;

    @NotNull(message = "Hospital id is mandatory")
    @JsonProperty("hospital_id")
    private Integer hospitalId;

    @NotNull(message = "Specialty id is mandatory")
    @JsonProperty("specialty_id")
    private Integer specialtyId;
}
