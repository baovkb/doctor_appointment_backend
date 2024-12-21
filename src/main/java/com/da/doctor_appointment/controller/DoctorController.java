package com.da.doctor_appointment.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.da.doctor_appointment.dto.request.DoctorSignupRequest;
import com.da.doctor_appointment.service.DoctorService;

@RestController
@RequestMapping("api/doctors")
public class DoctorController {
    @Autowired
    private DoctorService doctorService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping()
    public ResponseEntity<?> createDoctor(@RequestBody @Valid DoctorSignupRequest request) {
        doctorService.createDoctor(request);
        return new ResponseEntity<>("Signup successful", HttpStatus.OK);
    }
}
