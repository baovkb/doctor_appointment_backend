package com.da.doctor_appointment.dto;

import java.time.LocalTime;


import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleDTO {
    private Long id;

    @JsonProperty("day_of_week")
    @NotNull(message= "Day is mandatory")
    private Integer dayOfWeek;

    @JsonProperty("start_time")
    @NotBlank(message = "Start time is mandatory")
    private LocalTime startTime;

    @JsonProperty("end_time")
    @NotBlank(message = "End time is mandatory")
    private LocalTime endTime;

    @JsonProperty("duration_per_slot")
    @NotNull(message = "Duration is mandatory")
    private Integer durationPerSlot;

    @NotNull(message = "Capacity is mandatory")
    private Integer capacity;

    @JsonProperty("doctor_id")
    private String doctorId;
}
