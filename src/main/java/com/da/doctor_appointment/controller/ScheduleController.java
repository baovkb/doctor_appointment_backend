package com.da.doctor_appointment.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.da.doctor_appointment.dto.ScheduleDTO;
import com.da.doctor_appointment.service.ScheduleService;

@RestController
@RequestMapping("api/schedules")
public class ScheduleController {
    @Autowired
    private ScheduleService scheduleService;

    @PreAuthorize("hasRole('PATIENT')")
    @GetMapping()
    public ResponseEntity<?> getAllSchedules() {
        List<ScheduleDTO> scheduleDTOs = scheduleService.getAllSchedules();
        return new ResponseEntity<>(scheduleDTOs, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('DOCTOR')")
    @PostMapping()
    public ResponseEntity<?> createSchedule(@RequestBody @Valid ScheduleDTO request) {
        ScheduleDTO schedule = scheduleService.createSchedule(request);
        return new ResponseEntity<>(schedule, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('DOCTOR')")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateSchedule(@PathVariable Integer id, @RequestBody @Valid ScheduleDTO request) {
        ScheduleDTO schedule = scheduleService.updateSchedule(id, request);
        return new ResponseEntity<>(schedule, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('DOCTOR')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSchedule(@PathVariable Integer id) {
        scheduleService.deleteSchedule(id);
        return new ResponseEntity<>("Delete schedule successful", HttpStatus.OK);
    }
}
