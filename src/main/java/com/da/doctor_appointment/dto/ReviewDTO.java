package com.da.doctor_appointment.dto;

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
public class ReviewDTO {
    private Integer id;

    @NotNull(message = "Star is mandatory")
    private Float star;
    @NotBlank(message = "Content is mandatory")
    private String content;

    @JsonProperty("post_date")
    private LocalDateTime postDate;

    @JsonProperty("doctor_id")
    private String doctorId;

    @NotNull(message = "Appointment id is mandatory")
    @JsonProperty("appointment_id")
    private Integer appointmentId;
}
