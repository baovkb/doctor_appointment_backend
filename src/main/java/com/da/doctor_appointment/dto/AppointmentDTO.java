package com.da.doctor_appointment.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentDTO {
    private Integer id;

    @JsonProperty("registration_time")
    private LocalDateTime registrationTime;

    @JsonProperty("update_time")
    private LocalDateTime updateTime;

    private String status;

    @NotBlank(message = "Appointment date is mandatory")
    @JsonProperty("appointment_date")
    private LocalDate appointmentDate;

    @JsonProperty("time_slot_id")
    @NotNull(message = "Time slot id is mandatory")
    private Integer timeSlotId;
}
