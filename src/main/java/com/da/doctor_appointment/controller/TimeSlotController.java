package com.da.doctor_appointment.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.da.doctor_appointment.dto.TimeSlotDTO;
import com.da.doctor_appointment.service.TimeSlotService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("api/schedules/{scheduleId}/timeslots")
public class TimeSlotController {
    @Autowired
    private TimeSlotService timeSlotService;

    @PreAuthorize("hasRole('PATIENT')")
    @GetMapping()
    public ResponseEntity<?> getMethodName(@PathVariable(name = "scheduleId") Integer id) {
        List<TimeSlotDTO> timeSlotDTOs = timeSlotService.getAllAvailableTimeSlots(id);
        return new ResponseEntity<>(timeSlotDTOs, HttpStatus.OK);
    }
    
}
