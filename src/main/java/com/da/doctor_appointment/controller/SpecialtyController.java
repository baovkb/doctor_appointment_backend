package com.da.doctor_appointment.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.da.doctor_appointment.dto.SpecialtyDTO;
import com.da.doctor_appointment.service.ScheduleService;
import com.da.doctor_appointment.service.SpecialtyService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
@RequestMapping("api/specialties")
public class SpecialtyController {
    @Autowired
    private SpecialtyService specialtyService;

    @Autowired
    private ScheduleService scheduleService;

    // @PreAuthorize("hasRole('PATIENT')")
    @GetMapping()
    public ResponseEntity<?> getAllSpecialties() {
        List<SpecialtyDTO> specialtyDTOs = specialtyService.getAllSpecialties();
        return new ResponseEntity<>(specialtyDTOs, HttpStatus.OK);
    }

    // @PreAuthorize("hasRole('PATIENT')")
    @GetMapping("{specialtyId}/schedules")
    public ResponseEntity<?> getSchedulesBySpecialtyId(@PathVariable Integer specialtyId) {
        return new ResponseEntity<>(scheduleService.getSchedulesBySpecialtyId(specialtyId), HttpStatus.OK);
    }
    
}
