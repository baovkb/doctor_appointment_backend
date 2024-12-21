package com.da.doctor_appointment.dto.request;

import jakarta.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ReceptionistSignupRequest extends SignupRequest{
    @NotNull(message = "Hospital id is mandatory")
    @JsonProperty("hospital_id")
    private Integer hospitalId;
}
